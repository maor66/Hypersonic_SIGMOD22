package sase.base;

public interface ContainsEvent
{
    long getTimestamp();
    boolean isLastInput();

    long getSequenceNumber();
}
