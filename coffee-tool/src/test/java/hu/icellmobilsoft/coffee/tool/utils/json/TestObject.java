/*-
 * #%L
 * Sampler
 * %%
 * Copyright (C) 2022 - 2024 i-Cell Mobilsoft Zrt.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package hu.icellmobilsoft.coffee.tool.utils.json;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Test object used in json util test cases
 *
 * @author bucherarnold
 * @since 2.9.0
 */
public class TestObject {

    XMLGregorianCalendar xmlGregorianCalendar;
    Date date;
    byte[] bytes;
    String string;
    Class<?> clazz;
    OffsetDateTime offsetDateTime;
    OffsetTime offsetTime;
    LocalDate localDate;
    Duration duration;
    YearMonth yearMonth;

    public XMLGregorianCalendar getXmlGregorianCalendar() {
        return xmlGregorianCalendar;
    }

    public void setXmlGregorianCalendar(XMLGregorianCalendar xmlGregorianCalendar) {
        this.xmlGregorianCalendar = xmlGregorianCalendar;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public OffsetDateTime getOffsetDateTime() {
        return offsetDateTime;
    }

    public void setOffsetDateTime(OffsetDateTime offsetDateTime) {
        this.offsetDateTime = offsetDateTime;
    }

    public OffsetTime getOffsetTime() {
        return offsetTime;
    }

    public void setOffsetTime(OffsetTime offsetTime) {
        this.offsetTime = offsetTime;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public YearMonth getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(YearMonth yearMonth) {
        this.yearMonth = yearMonth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TestObject)) {
            return false;
        }
        TestObject other = (TestObject) o;

        return Objects.equals(xmlGregorianCalendar, other.xmlGregorianCalendar) //
                && Objects.equals(date, other.date)//
                && Objects.deepEquals(bytes, other.bytes)//
                && Objects.equals(string, other.string)//
                && Objects.equals(clazz, other.clazz) && Objects.equals(offsetDateTime, other.offsetDateTime)
                && Objects.equals(offsetTime, other.offsetTime) && Objects.equals(localDate, other.localDate)
                && Objects.equals(duration, other.duration)
                && Objects.equals(yearMonth, other.yearMonth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(xmlGregorianCalendar, date, bytes, string, clazz, offsetDateTime, offsetTime, localDate, duration, yearMonth);
    }

    @Override
    public String toString() {
        return "TestObject{" + "xmlGregorianCalendar=" + xmlGregorianCalendar + ", date=" + date + ", bytes=" + Arrays.toString(bytes)
                + ", string='" + string + '\'' + ", clazz=" + clazz + "offsetDateTime=" + offsetDateTime + ", offsetTime=" + offsetTime
                + ", localDate=" + localDate + ", duration=" + duration + ", yearMonth=" + yearMonth + '}';
    }

}
