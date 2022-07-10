package org.dows.framework.crud.es;

import lombok.Data;

import java.util.Map;

@Data
public class SearchRequestQuery {
    private String esIndex;
    private Integer pageNo;
    private Integer pageSize;
    private String[] keywordFields;
    private String sortBy;
    private Boolean isDesc;
    private Map<String, String> aggMap;
    private String sourceFilter;
    private Map<String, Map<String, String>> filter;
    private String dateField;
    private String startDate;
    private String endDate;
    private String keyword;
}
