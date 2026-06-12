/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Generated;

public class PageResult<T>
implements Serializable {
    private List<T> records;
    private Long total;
    private Long current;
    private Long size;
    private Long pages;

    public PageResult() {
    }

    public PageResult(List<T> records, Long total, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (total + size - 1L) / size;
    }

    @Generated
    public List<T> getRecords() {
        return this.records;
    }

    @Generated
    public Long getTotal() {
        return this.total;
    }

    @Generated
    public Long getCurrent() {
        return this.current;
    }

    @Generated
    public Long getSize() {
        return this.size;
    }

    @Generated
    public Long getPages() {
        return this.pages;
    }

    @Generated
    public void setRecords(List<T> records) {
        this.records = records;
    }

    @Generated
    public void setTotal(Long total) {
        this.total = total;
    }

    @Generated
    public void setCurrent(Long current) {
        this.current = current;
    }

    @Generated
    public void setSize(Long size) {
        this.size = size;
    }

    @Generated
    public void setPages(Long pages) {
        this.pages = pages;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PageResult)) {
            return false;
        }
        PageResult other = (PageResult)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$total = this.getTotal();
        Long other$total = other.getTotal();
        if (this$total == null ? other$total != null : !((Object)this$total).equals(other$total)) {
            return false;
        }
        Long this$current = this.getCurrent();
        Long other$current = other.getCurrent();
        if (this$current == null ? other$current != null : !((Object)this$current).equals(other$current)) {
            return false;
        }
        Long this$size = this.getSize();
        Long other$size = other.getSize();
        if (this$size == null ? other$size != null : !((Object)this$size).equals(other$size)) {
            return false;
        }
        Long this$pages = this.getPages();
        Long other$pages = other.getPages();
        if (this$pages == null ? other$pages != null : !((Object)this$pages).equals(other$pages)) {
            return false;
        }
        List<T> this$records = this.getRecords();
        List<T> other$records = other.getRecords();
        return !(this$records == null ? other$records != null : !((Object)this$records).equals(other$records));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof PageResult;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $total = this.getTotal();
        result = result * 59 + ($total == null ? 43 : ((Object)$total).hashCode());
        Long $current = this.getCurrent();
        result = result * 59 + ($current == null ? 43 : ((Object)$current).hashCode());
        Long $size = this.getSize();
        result = result * 59 + ($size == null ? 43 : ((Object)$size).hashCode());
        Long $pages = this.getPages();
        result = result * 59 + ($pages == null ? 43 : ((Object)$pages).hashCode());
        List<T> $records = this.getRecords();
        result = result * 59 + ($records == null ? 43 : ((Object)$records).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "PageResult(records=" + String.valueOf(this.getRecords()) + ", total=" + this.getTotal() + ", current=" + this.getCurrent() + ", size=" + this.getSize() + ", pages=" + this.getPages() + ")";
    }
}

