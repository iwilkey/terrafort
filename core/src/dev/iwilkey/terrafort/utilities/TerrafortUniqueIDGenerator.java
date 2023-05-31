package dev.iwilkey.terrafort.utilities;

import dev.iwilkey.terrafort.Terrafort;

/**
 * Unique identification long generator, inspired by Twitter's "Snowflake Identification Algorithm."
 * Author: iwilkey
 */
public final class TerrafortUniqueIDGenerator {

    /**
     * Number of bits for sequence.
     */
    public static final long SEQUENCE_BITS = 12L;

    /**
     * Number of bits for worker ID.
     */
    public static final long WORKER_ID_BITS = 5L;

    /**
     * Number of bits for data center ID.
     */
    public static final long DATA_CENTER_ID_BITS = 5L;

    /**
     * Maximum worker ID.
     */
    public static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);

    /**
     * Maximum data center ID.
     */
    public static final long MAX_DATA_CENTER_ID = -1L ^ (-1L << DATA_CENTER_ID_BITS);

    /**
     * Number of bits to shift for worker ID.
     */
    public static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /**
     * Number of bits to shift for data center ID.
     */
    public static final long DATA_CENTER_ID_SHIFT = WORKER_ID_SHIFT + WORKER_ID_BITS;

    /**
     * Number of bits to shift for timestamp.
     */
    public static final long TIMESTAMP_LEFT_SHIFT = DATA_CENTER_ID_SHIFT + DATA_CENTER_ID_BITS;

    /**
     * Bit mask for sequence.
     */
    public static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);

    /**
     * Count of allocated generators.
     */
    public static long allocatedGenerators = 0;

    private long workerId;
    private long dataCenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    /**
     * Constructs a SnowflakeIDGenerator with default worker ID and data center ID.
     */
    public TerrafortUniqueIDGenerator() {
        allocatedGenerators++;
        this.workerId = allocatedGenerators;
        this.dataCenterId = allocatedGenerators;
    }

    /**
     * Generates the next unique ID.
     *
     * @return The generated ID.
     */
    public synchronized long next() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp)
            Terrafort.fatal("Clock moved backwards. Refusing to generate ID for " + (lastTimestamp - timestamp) + " milliseconds");
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
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
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

}
