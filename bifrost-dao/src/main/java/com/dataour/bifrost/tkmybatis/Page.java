package com.dataour.bifrost.tkmybatis;

import java.util.List;

/****************************************
 * @@CREATE : 2018-02-02 下午9:48
 * @@AUTH : NOT A CAT【NOTACAT@CAT.ORZ】
 * @@DESCRIPTION : 分页相关
 * @@VERSION :
 *
 *****************************************/
public class Page<T> {
    /**
     * 总记录数
     */
    private long total;
    /**
     * 当前页
     */
    private int curPage;
    /**
     * 每页的数量
     */
    private int pageSize;
    /**
     * 总页数
     */
    private int pages;
    /**
     * 当前页的数量
     */
    private int size;
    /**
     * 内容列表
     */
    private List<T> contents;

    public Page(){

    }

    public Page(long total, int curPage, int pageSize, int pages, int size, List<T> contents) {
        this.total = total;
        this.curPage = curPage;
        this.pageSize = pageSize;
        this.pages = pages;
        this.size = size;
        this.contents = contents;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<T> getContents() {
        return contents;
    }

    public void setContents(List<T> contents) {
        this.contents = contents;
    }
}
