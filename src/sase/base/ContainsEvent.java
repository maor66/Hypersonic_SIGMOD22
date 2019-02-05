package sase.base;

public interface ContainsEvent
{
    long getTimestamp();

    long getSequenceNumber();

    long getEarliestTimestamp();

    boolean isLastInput();
}
