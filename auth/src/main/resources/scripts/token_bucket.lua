
local key = KEYS[1]

local capacity = 60
local refill_rate = 1
local ttl = 120

-- get current redis server time
local time = redis.call('TIME')
local current_time = tonumber(time[1]) + tonumber(time[2]) / 1000000


-- Read existing bucket state
local bucket = redis.call('HMGET', key, 'tokens', 'last_refill')

local tokens = tonumber(bucket[1])
local last_refill = tonumber(bucket[2])


-- Initialize new bucket if it does not exist
if tokens == nil or last_refill == nil then
	tokens = capacity
	last_refill = current_time
else
-- Calculate elapsed time and refill tokens
	local elapsed = math.max(0, current_time - last_refill)
	local refill = elapsed * refill_rate

	tokens = math.min(capacity, tokens + refill)
	last_refill = current_time
end


-- Check whether the request is allowed
local allowed = 0

if tokens >= 1 then
	tokens = tokens - 1
	allowed = 1
end


-- Persist the updated bucket state.
redis.call(
	'HSET',
	key,
	'tokens', tokens,
	'last_refill', last_refill
)


-- Refresh expiration time.
redis.call('EXPIRE', key, ttl)

return allowed
