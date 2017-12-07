package com.ppm.trace.trace;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.MDC;
import org.slf4j.Marker;

/**
 * Compare against a log level that is associated with an MDC value.
 */
public  class DynamicThresholdUserFilter extends TurboFilter{

    private String httpHeader = "X-LOG-LEVEL";

    public DynamicThresholdUserFilter(){
        setName("dynamicFilter");
    }

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        String value = MDC.get(httpHeader);
        if (null == value) {
            return FilterReply.NEUTRAL;
        }
        Level logLvl = Level.toLevel(value,Level.OFF);
        if (logLvl.equals(Level.OFF)) {
            return FilterReply.NEUTRAL;
        }
        if(level.levelInt >= logLvl.levelInt){
            return FilterReply.ACCEPT;
        }else{
            return FilterReply.DENY;
        }
    }
}