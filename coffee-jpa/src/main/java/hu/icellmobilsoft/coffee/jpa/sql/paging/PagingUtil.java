/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.jpa.sql.paging;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import hu.icellmobilsoft.coffee.exception.BaseException;
import hu.icellmobilsoft.coffee.exception.InvalidParameterException;

/**
 * <p>
 * PagingUtil class.
 * </p>
 *
 * @author Karoly
 * @since 1.0.0
 */
public final class PagingUtil {

    /** Constant <code>DEFAULT_ROWS=6</code> */
    public static final long DEFAULT_ROWS = 6;

    /** Constant <code>DEFAULT_PAGE_MAX_VALUE=21_474_836</code> coming from common.xsd#PageType */
    public static final long DEFAULT_PAGE_MAX_VALUE = 21_474_836;

    /** Constant <code>DEFAULT_ROWS_MAX_VALUE=100</code> coming from common.xsd#RowsType */
    public static final long DEFAULT_ROWS_MAX_VALUE = 100;

    private PagingUtil() {
    }

    /**
     * Returns with the query details and the paging result.
     *
     * @param query
     *            Main query.
     * @param count
     *            Total count.
     * @param page
     *            Page index.
     * @param rows
     *            Row counter.
     * @param <T>
     *            entity
     * @return Paging result.
     * @throws BaseException
     *             on error
     */
    public static <T> PagingResult<T> getPagingResult(final TypedQuery<T> query, final Long count, long page, long rows) throws BaseException {
        validatePage(page);
        validateRows(rows);
        return getPagingResultAndSetQueryDetails(query, count, page, rows, new QueryMetaData());
    }

    /**
     * Sets the query details and returns with the paging result.
     *
     * @param query
     *            Main query.
     * @param count
     *            total row count, can be null
     * @param page
     *            Page index.
     * @param rows
     *            Row counter.
     * @param details
     *            Query details.
     * @return Paging result.
     */
    private static <T> PagingResult<T> getPagingResultAndSetQueryDetails(final TypedQuery<T> query, Long count, long page, long rows,
            QueryMetaData details) {
        if (query == null || details == null) {
            return null;
        }

        long currentPage = getCurrentPage(page);
        final long currentRows = getCurrentRows(rows);

        PagingResult<T> result = new PagingResult<>();

        if (currentPage != -1) {
            long maxCount;
            if (count != null) {
                maxCount = count;
            } else {
                maxCount = query.getResultList().size();
            }
            long maxPage = getPageCount(maxCount, currentRows);

            long resultCount = 0;

            if (currentPage > 0 && currentPage <= maxPage) {
                query.setMaxResults((int) currentRows);
                query.setFirstResult((int) (currentRows * (currentPage - 1)));

                List<T> resultList = query.getResultList();
                result.setResults(resultList);
                resultCount = resultList.size();
            } else if (currentPage == -1) {
                // Returns all result
                List<T> resultList = query.getResultList();
                result.setResults(resultList);
                resultCount = resultList.size();

                // Updated for result
                currentPage = 1;
            } else {
                result.setResults(new ArrayList<>());
            }

            setDetails(details, currentPage, maxCount, maxPage, resultCount, result);
        } else {
            // Returns all result
            List<T> resultList = query.getResultList();
            result.setResults(resultList);

            setDetails(details, 1, resultList.size(), 1, resultList.size(), result);
        }

        return result;
    }

    /**
     * Returns with the query details and the paging result(for native solutions).
     *
     * @param query
     *            Main query.
     * @param countQuery
     *            Separate query for counting.
     * @param page
     *            Page index.
     * @param rows
     *            Row counter.
     * @param <T>
     *            Entity.
     * @return Paging result.
     * @throws BaseException
     *             on error
     */
    public static <T> PagingResult<T> getPagingResult(final Query query, final Query countQuery, long page, long rows) throws BaseException {
        validatePage(page);
        validateRows(rows);
        return getPagingResultAndSetQueryDetails(query, countQuery, page, rows, new QueryMetaData());
    }

    /**
     * Sets the query details and returns with the paging result (for native solutions).
     *
     * @param query
     *            Main query.
     * @param countQuery
     *            Separate query for counting.
     * @param page
     *            Page index.
     * @param rows
     *            Row counter.
     * @param details
     *            Query details.
     * @param <T>
     *            Entity.
     * @return Paging result.
     * @throws BaseException
     *             on error
     */
    @SuppressWarnings("unchecked")
    public static <T> PagingResult<T> getPagingResultAndSetQueryDetails(final Query query, final Query countQuery, long page, long rows,
            QueryMetaData details) throws BaseException {
        if (query == null || details == null) {
            return null;
        }
        validatePage(page);
        validateRows(rows);
        long currentPage = getCurrentPage(page);
        final long currentRows = getCurrentRows(rows);

        PagingResult<T> result = new PagingResult<>();

        if (currentPage != -1) {
            long maxCount;
            if (countQuery != null) {
                maxCount = ((Number) countQuery.getSingleResult()).longValue();
            } else {
                maxCount = query.getResultList().size();
            }
            long maxPage = getPageCount(maxCount, currentRows);

            long resultCount = 0;
            if (currentPage > 0 && currentPage <= maxPage) {
                query.setMaxResults((int) currentRows);
                query.setFirstResult((int) (currentRows * (currentPage - 1)));

                List<T> resultList = query.getResultList();
                result.setResults(resultList);
                resultCount = resultList.size();
            } else {
                result.setResults(new ArrayList<>());
            }

            setDetails(details, currentPage, maxCount, maxPage, resultCount, result);
        } else {
            List<T> resultList = query.getResultList();
            result.setResults(resultList);

            setDetails(details, 1, resultList.size(), 1, resultList.size(), result);
        }

        return result;
    }

    /**
     * Creates the {@link QueryMetaData} with the given parameters.
     *
     * @param maxCount
     *            Total row count.
     * @param resultCount
     *            Total result count.
     * @param page
     *            Page index.
     * @param rows
     *            Row counter.
     * @return Query metadata.
     * @throws BaseException
     *             on error
     */
    public static QueryMetaData createDetails(final long maxCount, final long resultCount, final long page, final long rows) throws BaseException {
        validatePage(page);
        validateRows(rows);
        long currentPage = PagingUtil.getCurrentPage(page);
        final long currentRows = PagingUtil.getCurrentRows(rows);
        long maxPage = PagingUtil.getPageCount(maxCount, currentRows);
        QueryMetaData details = new QueryMetaData();
        details.setRows(BigInteger.valueOf(resultCount));
        details.setPage(BigInteger.valueOf(currentPage));
        details.setTotalRows(BigInteger.valueOf(maxCount));
        details.setMaxPage(BigInteger.valueOf(maxPage));
        return details;
    }

    /**
     * Returns the number of pages.
     *
     * @param sum
     *            Total result count.
     * @param rows
     *            Number of rows per page.
     * 
     * @return Page count.
     */
    public static long getPageCount(long sum, long rows) {
        return (rows <= 0) ? 0L : (long) Math.ceil((double) sum / (double) rows);
    }

    private static long getCurrentPage(final long page) {
        return (page == 0) ? 1 : page;
    }

    private static long getCurrentRows(final long rows) {
        return (rows == 0) ? DEFAULT_ROWS : rows;
    }

    private static <T> void setDetails(QueryMetaData details, final long currentPage, long maxCount, long maxPage, long resultCount,
            PagingResult<T> result) {
        details.setRows(BigInteger.valueOf(resultCount));
        details.setPage(BigInteger.valueOf(currentPage));
        details.setTotalRows(BigInteger.valueOf(maxCount));
        details.setMaxPage(BigInteger.valueOf(maxPage));
        result.setDetails(details);
    }

    private static void validatePage(long page) throws BaseException {
        if (page > DEFAULT_PAGE_MAX_VALUE || page < 1) {
            throw new InvalidParameterException("Invalid 'page' value!");
        }
    }

    private static void validateRows(long rows) throws BaseException {
        if (rows > DEFAULT_ROWS_MAX_VALUE || rows < 1) {
            throw new InvalidParameterException("Invalid 'rows' value!");
        }
    }
}
