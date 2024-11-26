package com.dataour.bifrost.api.common.util.uuid;

/**
 * 生成有序uuid
 *
 * @Author JASON
 * @Date 2023年03月17日 14:46
 */
public class UUIDGenerator extends AbstractUUIDGenerator {

    private static final String SEP = "-";

    public static String genUUIDWithSep() {
        return format(getJVM()) + SEP
                + format(getHiTime()) + SEP
                + format(getLoTime()) + SEP
                + format(getIP()) + SEP
                + format(getCount());
    }

    public static String genUUID() {
        return format(getJVM())
                + format(getHiTime())
                + format(getLoTime())
                + format(getIP())
                + format(getCount());
    }

    protected static String format(int intValue) {
        String formatted = Integer.toHexString(intValue);
        StringBuilder buf = new StringBuilder("00000000");
        buf.replace(8 - formatted.length(), 8, formatted);
        return buf.toString();
    }

    protected static String format(short shortValue) {
        String formatted = Integer.toHexString(shortValue);
        StringBuilder buf = new StringBuilder("0000");
        buf.replace(4 - formatted.length(), 4, formatted);
        return buf.toString();
    }
}
