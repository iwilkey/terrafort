package dev.iwilkey.terrafort.utilities;

/**
 * Unique identification long generator, inspried by Twitter's "Snowflake Identification Algorithm."
 * @author iwilkey
 */
public final class SnowflakeIDGenerator {
	
	public static final long SEQUENCE_BITS = 12L;
	public static final long WORKER_ID_BITS = 5L;
	public static final long DATA_CENTER_ID_BITS = 5L;
	public static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);
	public static final long MAX_DATA_CENTER_ID = -1L ^ (-1L << DATA_CENTER_ID_BITS);
	public static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
	public static final long DATA_CENTER_ID_SHIFT = WORKER_ID_SHIFT + WORKER_ID_BITS;
	public static final long TIMESTAMP_LEFT_SHIFT = DATA_CENTER_ID_SHIFT + DATA_CENTER_ID_BITS;
	public static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);
	
	public static long allocatedGenerators = 0;
	
	private long workerId;
    private long dataCenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
	
	public SnowflakeIDGenerator() {
		allocatedGenerators++;
        this.workerId = allocatedGenerators;
        this.dataCenterId = allocatedGenerators;
	}
	
	public synchronized long next() {
        long timestamp = timeGen();
        if(timestamp < lastTimestamp) {
            throw new RuntimeException("[Terrafort Engine] Clock moved backwards. Refusing to generate ID for " + (lastTimestamp - timestamp) + " milliseconds");
        }
        if(lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if(sequence == 0) {
                timestamp = tillNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp << TIMESTAMP_LEFT_SHIFT) | 
        		(dataCenterId << DATA_CENTER_ID_SHIFT) | 
        		(workerId << WORKER_ID_SHIFT) | sequence);
    }
	
	private long tillNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while(timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }
	
	private long timeGen() {
        return System.currentTimeMillis();
    }
	
}
