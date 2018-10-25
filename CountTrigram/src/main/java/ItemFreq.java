import org.apache.hadoop.io.*;

import java.io.File;
import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.Iterator;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;
import java.lang.Math;

public class ItemFreq {
//implements Comparable<ItemFreq> {

    private String item;
    private Long freq;

    /**
     * Constructor.
     */
    public ItemFreq() { 
      this.item = "";
      this.freq = 0L;
    }

    /**
     * Constructor.
     */
    public ItemFreq(String item, long freq) {
            this.item = item;
            this.freq = freq;
    }

    @Override
    public String toString() {
            return (new StringBuilder())
                            .append('{')
                            .append(item)
                            .append(',')
                            .append(freq)
                            .append('}')
                            .toString();
    }

    public String getItem() {
            return item;
    }

    public void setItem(String item) {
            this.item = item;
    }

    public Long getFreq() {
            return freq;
    }

    public void setFreq(Long freq) {
            this.freq = freq;
    }
}

