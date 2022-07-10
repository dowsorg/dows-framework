package org.dows.framework.crud.es;


import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class ElasticSearchRestClient {
    @Autowired
    private RestHighLevelClient restHighLevelClient;


    public boolean indexExist(String indexName) {
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);

        try {
            return restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }


    public void createIndex(String indexName) {

        if (!indexExist(indexName)) {
            CreateIndexRequest indexRequest = new CreateIndexRequest(indexName);


            try {
                restHighLevelClient.indices().create(indexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log.info("==index已存在==");
        }

    }

    public String getEsIdbyQuerys(String indexName, Map<String, Object> querys) {
        String id = "";
        SearchRequest request = new SearchRequest(indexName);
        //构建查询
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        for (Map.Entry<String, Object> query : querys.entrySet()) {
            String queryName = query.getKey();
            Object queryValue = query.getValue();
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.should(QueryBuilders.termQuery(queryName, queryValue));
            sourceBuilder.query(boolQueryBuilder);

        }

        request.source(sourceBuilder);
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //   long value = response.getHits().getTotalHits().value;
        SearchHit[] hits = response.getHits().getHits();

        for (SearchHit hit : hits) {
            id = hit.getId();
        }
        return id;
    }

    public Boolean save(String indexName, List<Map<String, Object>> datas) {
        BulkRequest request = new BulkRequest();
        datas.forEach(data -> {
            request.add(new IndexRequest(indexName).source(data, XContentType.JSON));
        });
        BulkResponse bulkResponse = null;
        try {
            bulkResponse = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean b = bulkResponse.hasFailures();
        log.info("保存返回:{}", b);
        return b;
    }

    public void updateByQuery(String indexName, Map<String, Object> queryParams, Map<String, Object> updateScript) {
        UpdateByQueryRequest updateByQuery = new UpdateByQueryRequest(indexName);
        //设置分片并行
        updateByQuery.setSlices(2);
        //设置版本冲突时继续执行
        updateByQuery.setConflicts("proceed");
        //设置更新完成后刷新索引 ps很重要如果不加可能数据不会实时刷新
        updateByQuery.setRefresh(true);
        //查询条件如果是and关系使用must 如何是or关系使用should
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termQuery(key, value));
            updateByQuery.setQuery(boolQueryBuilder);
        }


        //设置要修改的内容可以多个值多个用；隔开
        String code = "";
        for (Map.Entry<String, Object> entry : updateScript.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            code = code + "ctx._source['" + key + "']='" + value + "';";


//                    "ctx._source['submitTime']='" + smsSendReceipt.getReceiveTime() + "'" +
//                    ";ctx._source['errCode']='" + smsSendReceipt.getErrCode() + "'" +
//                    ";ctx._source['receiveTime']='" + smsSendReceipt.getReceiveTime() + "'" +
//                    ";ctx._source['channelProviderName']='" + smsSendReceipt.getChannelProviderName() + "'"));
        }
        updateByQuery.setScript(new Script(code));
        try {
            BulkByScrollResponse response = restHighLevelClient.updateByQuery(updateByQuery, RequestOptions.DEFAULT);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public <T> List<T> getHits(SearchResponse response, Class<T> clazz) {
        List<T> messageList = new ArrayList<>();
        response.getHits().forEach(hit -> messageList.add(JSONUtil.toBean(hit.getSourceAsString(), clazz)));
        return messageList;
    }

    public SearchResponse search(String indexName, Map<String, Object> queryMap) {

        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder builder = new SearchSourceBuilder();


        for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.must(QueryBuilders.termQuery(key, value));
            builder.query(boolQueryBuilder);
        }
        searchRequest.source(builder);
        SearchResponse searchResponse = null;


        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResponse;

    }

//    /**
//     * 构建查询对象
//     *
//     * @param filedsMap 查询条件 (key:查询字段 ,vlues:值)
//     * @return
//     */
//    public BoolQueryBuilder getQueryBuilder(Map<String, String> filedsMap) {
//
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        Set<String> strings = filedsMap.keySet();
//        for (String string : strings) {
//            boolQueryBuilder.must(QueryBuilders.wildcardQuery(string, "*" + filedsMap.get(string) + "*"));
//        }
//        return boolQueryBuilder;
//    }
//
//    /**
//     * 获取分页后的结果集
//     *
//     * @param queryBuilder 查询对象
//     * @param esIndex      索引名
//     * @param pageNo       页数
//     * @param pagesize     页大小
//     * @param glFields     需要高亮显示的字段
//     * @return
//     */
//    public List<Map<String, Object>> getPageResultList(QueryBuilder queryBuilder, String esIndex, int pageNo, int pagesize, List<String> glFields) {
//        SearchRequest searchRequest = new SearchRequest(esIndex);
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//
//        //设置高亮显示
//        HighlightBuilder highlightBuilder = new HighlightBuilder().field("*").requireFieldMatch(false);
//        highlightBuilder.preTags("<span style=\"color:red\">");
//        highlightBuilder.postTags("</span>");
//        searchSourceBuilder.highlighter(highlightBuilder);
//
//        if (pageNo >= 1) {
//            searchSourceBuilder.query(queryBuilder).from((pageNo - 1) * pagesize).size(pagesize);
//        } else {
//            searchSourceBuilder.query(queryBuilder).from(0).size(pagesize);
//        }
//        searchRequest.source(searchSourceBuilder);
//
//        SearchResponse searchResponse = null;
//
//        try {
//            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // 从response中获得结果
//        List<Map<String, Object>> list = new LinkedList();
//        searchResponse.getHits();
//
//        SearchHits hits = searchResponse.getHits();
//
//        Iterator<SearchHit> iterator = hits.iterator();
//        while (iterator.hasNext()) {
//            SearchHit next = iterator.next();
//            Map<String, Object> source = next.getSourceAsMap();
//            //处理高亮片段
//            Map<String, HighlightField> highlightFields = next.getHighlightFields();
//            for (String fieldName : glFields) {
//                HighlightField nameField = highlightFields.get(fieldName);
//                if (nameField != null) {
//                    Text[] fragments = nameField.fragments();
//                    StringBuilder nameTmp = new StringBuilder();
//                    for (Text text : fragments) {
//                        nameTmp.append(text);
//                    }
//                    //将高亮片段组装到结果中去
//                    source.put(fieldName, nameTmp.toString());
//                }
//            }
//            list.add(source);
//        }
//        return list;
//    }
//
//    /**
//     * 全文检索
//     *
//     * @param query
//     * @return
//     */
//    public Map<String, Object> search(SearchRequestQuery query) throws Exception {
//        //获取客户端
//
//        Map<String, Object> result = new HashMap<>();
//        List<Map<String, Object>> list = new ArrayList<>();
//        // 1、创建查询索引
//        SearchRequest searchRequest = new SearchRequest(query.getEsIndex());
//        // 2、条件查询
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        //3.构建分页
//        int pageNo = 1, pageSize = 10;
//        if (query.getPageNo() != null) {
//            pageNo = query.getPageNo();
//        }
//        if (query.getPageSize() != null) {
//            pageSize = query.getPageSize();
//        }
//        //3.1 es默认从第0页开始
//        sourceBuilder.from((pageNo - 1) * pageSize);
//        sourceBuilder.size(pageSize);
//        //4.构建基础查询（包含基础查询和过滤条件）【过滤关系，key为（and或者or或者not），value为过滤字段和值】
//        QueryBuilder queryBuilder = buildBasicQueryWithFilter(query);
//        sourceBuilder.query(queryBuilder);
//        //4.2 设置最长等待时间1分钟
//        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
//        // 5、高亮设置(替换返回结果文本中目标值的文本内容)
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        for (int i = 0; i < query.getKeywordFields().length; i++) {
//            highlightBuilder.field(query.getKeywordFields()[i]);
//        }
//        //5.1允许同一个检索词多次高亮，false则表示，同意字段中同一个检索词第一个位置的高亮，其他不高亮
//        highlightBuilder.requireFieldMatch(true);
//        highlightBuilder.preTags("<span style='color:red'>");
//        highlightBuilder.postTags("</span>");
//        sourceBuilder.highlighter(highlightBuilder);
//        //6.构建排序
//        String sortBy = query.getSortBy();
//        Boolean desc = query.getIsDesc();
//        if (StrUtil.isNotBlank(sortBy)) {
//            sourceBuilder.sort(new FieldSortBuilder(sortBy).order(desc ? SortOrder.DESC : SortOrder.ASC));
//        }
//        //7.聚合（分组）
//        Map<String, String> aggs = query.getAggMap();
//        if (aggs != null) {
//            for (Map.Entry<String, String> entry : aggs.entrySet()) {
//                //聚合名称(分组）
//                String aggName = entry.getKey();
//                //聚合字段
//                String aggFiled = entry.getValue();
//                if (aggName != null || aggFiled != null) {
//                    sourceBuilder.aggregation(AggregationBuilders.terms(aggName).field(aggFiled + ".keyword"));
//                }
//            }
//        }
//        //8、通过sourceFilter设置返回的结果字段,第一个参数是显示的字段，第二个参数是不显示的字段，默认设置为null
//        sourceBuilder.fetchSource(query.getSourceFilter(), null);
//        //9、执行搜索
//        searchRequest.source(sourceBuilder);
//        try {
//            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//            for (SearchHit doc : searchResponse.getHits().getHits()) {
//                // 解析高亮字段
//                Map<String, HighlightField> highlightFields = doc.getHighlightFields();
//                for (int i = 0; i < query.getKeywordFields().length; i++) {
//                    HighlightField fieldTitle = highlightFields.get(query.getKeywordFields()[i]);
//                    // 获取原来的结果集
//                    Map<String, Object> sourceAsMap = doc.getSourceAsMap();
//                    if (fieldTitle != null) {
//                        // 获取内容中匹配的片段
//                        Text[] fragments = fieldTitle.fragments();
//                        // 设置当前的目标字段为空
//                        String new_fieldTitle = "";
//                        for (Text res : fragments) {
//                            new_fieldTitle += res;
//                        }
//                        // 将原来的结果替换为新结果
//                        sourceAsMap.put(query.getKeywordFields()[i], new_fieldTitle);
//                    }
//                    list.add(sourceAsMap);
//                }
//            }
//            // List 数组去重， 多字段查询高亮解析的时候存在数组重复的情况（优化方法未知！）
//            list = list.stream().distinct().collect(Collectors.toList());
//            int total = (int) searchResponse.getHits().getTotalHits().value;
//            result.put("data", list);
//            result.put("total", total);
//            result.put("totalPage", total == 0 ? 0 : (total % pageSize == 0 ? total / pageSize : (total / pageSize) + 1));
//            result.put("pageSize", pageSize);
//            result.put("pageNo", pageNo);
//
//            //聚和结果处理
//            Aggregations aggregations = searchResponse.getAggregations();
//            List<Object> aggData = new ArrayList<>();
//            if (aggregations != null) {
//                aggData = getAggData(aggregations, query);
//            }
//            result.put("aggData", aggData);
//
//        } catch (IOException e) {
////            log.error(e);
//        } finally {
////            closeClient(client);
//        }
//        return result;
//    }
//
//    /**
//     * 聚合数据处理（分组）
//     *
//     * @param aggregations
//     * @param query
//     * @return
//     */
//    private static List<Object> getAggData(Aggregations aggregations, SearchRequestQuery query) {
//        List<Object> result = new ArrayList<>();
//        for (Map.Entry<String, String> entry : query.getAggMap().entrySet()) {
//            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
//            //聚合名称(分组）
//            String aggName = entry.getKey();
//            //聚合字段
//            String aggFiled = entry.getValue();
//            if (aggName != null) {
//                LinkedHashMap<String, Object> groupItem = new LinkedHashMap<>();
//                Terms aggregation = aggregations.get(aggName);
//                for (Terms.Bucket bucket : aggregation.getBuckets()) {
//                    map.put(bucket.getKey().toString(), bucket.getDocCount());
//                }
//                groupItem.put("aggregationName", aggName);
//                groupItem.put("aggregationField", aggFiled);
//                groupItem.put("aggregationData", map);
//                result.add(groupItem);
//            }
//        }
//        return result;
//    }
//
//    private QueryBuilder buildBasicQueryWithFilter(SearchRequestQuery query) {
//        String flag = "";
//        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
//        BoolQueryBuilder shouldQuery = QueryBuilders.boolQuery();
//        //过滤条件(and,or,not关系)
//        Map<String, Map<String, String>> filter = query.getFilter();
//        if (filter != null) {
//            for (Map.Entry<String, Map<String, String>> entry : filter.entrySet()) {
//                String key = entry.getKey();
//                flag = key;
//                Map<String, String> value = entry.getValue();
//                for (Map.Entry<String, String> map : value.entrySet()) {
//                    String filterKey = map.getKey();
//                    String filterValue = map.getValue();
//                    if (key == "and") {
//                        queryBuilder.filter(QueryBuilders.termQuery(filterKey, filterValue));
//                    }
//                    if (key == "or") {
//                        shouldQuery.should(QueryBuilders.termQuery(filterKey, filterValue));
//                    }
//                    if (key == "not") {
//                        queryBuilder.mustNot(QueryBuilders.termQuery(filterKey, filterValue));
//                    }
//                }
//            }
//        }
//        //过滤日期期间的值，比如2019-07-01到2019-07-17
//        if (StrUtil.isNotBlank(query.getDateField()) || StrUtil.isNotBlank(query.getStartDate()) || StrUtil.isNotBlank(query.getEndDate())) {
//            queryBuilder.must(QueryBuilders.rangeQuery(query.getDateField()).from(query.getStartDate()).to(query.getEndDate()));
//        }
//        //如果输入的查询条件为空，则查询所有数据
//        if (query.getKeyword() == null || "".equals(query.getKeyword())) {
//            queryBuilder.must(QueryBuilders.matchAllQuery());
//            return queryBuilder;
//        }
//        if (flag == "or") {
//            //配置中文分词器并指定并分词的搜索方式operator
//            queryBuilder.must(QueryBuilders.multiMatchQuery(query.getKeyword(), query.getKeywordFields()))
//                    //解决should和must共用不生效问题
//                    .must(shouldQuery);
//        } else {
//            //多字段查询，字段直接是or的关系
//            queryBuilder.must(QueryBuilders.multiMatchQuery(query.getKeyword(), query.getKeywordFields()));
//        	/*queryBuilder.must(QueryBuilders.multiMatchQuery(query.getKeyword(),query.getKeywordFields())
//                    .analyzer("ik_smart").operator(Operator.OR));*/
//        }
//        return queryBuilder;
//    }
//
//    public List<LinkedHashMap<String, Object>> getPageResultListLinked(QueryBuilder queryBuilder, String esIndex, int pageNo, int pagesize, SortBuilder sortBuilder, String[] includes, String[] excludes) {
//        SearchRequest searchRequest = new SearchRequest(esIndex);
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//
//        //searchSourceBuilder.query(queryBuilder).from((pageNo - 1) * pagesize).size(pagesize).sort(sortBuilder).fetchSource(includes,excludes);
//        if (sortBuilder != null) {
//            searchSourceBuilder.query(queryBuilder).from((pageNo - 1) * pagesize).size(pagesize).sort(sortBuilder);
//        } else {
//            searchSourceBuilder.query(queryBuilder).from((pageNo - 1) * pagesize).size(pagesize);
//        }
//        if (includes != null && includes.length > 0) {
//            searchSourceBuilder.fetchSource(includes, excludes);
//        }
//        searchRequest.source(searchSourceBuilder);
//
//        SearchResponse searchResponse = null;
//
//        try {
//            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // 从response中获得结果
//        List<LinkedHashMap<String, Object>> list = new LinkedList();
//        searchResponse.getHits();
//
//        SearchHits hits = searchResponse.getHits();
//
//        Iterator<SearchHit> iterator = hits.iterator();
//        while (iterator.hasNext()) {
//            SearchHit next = iterator.next();
//            list.add(getMapValueForLinkedHashMap(next.getSourceAsMap()));
//        }
//        return list;
//    }
//
//    public static LinkedHashMap getMapValueForLinkedHashMap(Map dataMap) {
//        LinkedHashMap returnMap = new LinkedHashMap();
//        Iterator iterator = dataMap.keySet().iterator();
//        while (iterator.hasNext()) {
//            Object objKey = iterator.next();
//            Object objValue = dataMap.get(objKey);
//            if (objValue instanceof Map) {
//                returnMap.put(objKey, getMapValueForLinkedHashMap((Map) objValue));
//            } else {
//                returnMap.put(toLowerCaseFirstOne(objKey.toString()), objValue);
//            }
//        }
//        return returnMap;
//    }
//
//    private static String toLowerCaseFirstOne(String s) {
//        if (Character.isLowerCase(s.charAt(0)))
//            return s;
//        else
//            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
//    }
//
//    /**
//     * 获取结果总数
//     *
//     * @param queryBuilder
//     * @param esIndex
//     * @return
//     */
//    public Long getResultCount(QueryBuilder queryBuilder, String esIndex) {
//        CountRequest countRequest = new CountRequest(esIndex);
//        countRequest.query(queryBuilder);
//        try {
//            CountResponse response = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
//            long length = response.getCount();
//            return length;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return 0L;
//
//
//    }
//
//    /**
//     * 获取文档总数
//     *
//     * @param index
//     * @return
//     */
//    public long getDocCount(String index) {
//        CountRequest countRequest = new CountRequest();
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
//        countRequest.source(searchSourceBuilder);
//        CountResponse countResponse = null;
//
//        try {
//            countResponse = restHighLevelClient
//                    .count(countRequest, RequestOptions.DEFAULT);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return countResponse.getCount();
//    }
//
//    /**
//     * 判断索引是否存在
//     *
//     * @param esIndex
//     * @return
//     */
//    public boolean isIndexExist(String esIndex) {
//        boolean isExists = true;
//        GetIndexRequest request = new GetIndexRequest(esIndex);
//        try {
//            isExists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
//            if (isExists) {
//                System.out.println(String.format("索引%s已存在", esIndex));
//
//            } else {
//                System.out.println(String.format("索引%s不存在", esIndex));
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return isExists;
//    }
//
//    /**
//     * 新建索引
//     *
//     * @param esIndex
//     * @param shards       分片数
//     * @param replications 副本数
//     * @param fileds       字段名->类型
//     */
//    public void createIndex(String esIndex, int shards, int replications, Map<String, String> fileds) {
//        if (!isIndexExist(esIndex)) {
//            try {
//                XContentBuilder builder = XContentFactory.jsonBuilder()
//                        .startObject()
//                        .field("properties")
//                        .startObject();
//                for (String s : fileds.keySet()) {
//                    builder.field(s).startObject().field("index", "true").field("type", fileds.get(s)).endObject();
//                }
//                builder.endObject().endObject();
//                CreateIndexRequest request = new CreateIndexRequest(esIndex);
//                request.settings(Settings.builder()
//                        .put("index.number_of_shards", shards)
//                        .put("index.number_of_replicas", replications)
//                ).mapping(builder);
//                CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
//                boolean acknowledged = createIndexResponse.isAcknowledged();
//                if (acknowledged) {
//                    System.out.println(String.format("索引%s创建成功", esIndex));
//                } else {
//                    System.out.println(String.format("索引%s创建失败", esIndex));
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println(String.format("索引%s已存在", esIndex));
//        }
//    }
//
//    /**
//     * 删除索引
//     *
//     * @param esIndex
//     */
//    public void deleteIndex(String esIndex) {
//        DeleteIndexRequest request = new DeleteIndexRequest(esIndex);
//
//        try {
//            AcknowledgedResponse deleteIndexResponse = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
//            boolean acknowledged = deleteIndexResponse.isAcknowledged();
//            if (acknowledged) {
//                System.out.println(String.format("索引%s已删除", esIndex));
//            } else {
//                System.out.println(String.format("索引%s删除失败", esIndex));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 根据id获取数据，返回map(字段名,字段值)
//     *
//     * @param esIndex
//     * @param id
//     * @return
//     */
//    public Map<String, Object> getDataById(String esIndex, String id) {
//        GetRequest request = new GetRequest(esIndex, id);
//        GetResponse response = null;
//        Map<String, Object> source = null;
//        try {
//            response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
//            if (response.isExists()) {
//                source = response.getSource();
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return source;
//
//
//    }
//
//    /**
//     * 更新文档
//     *
//     * @param esIndex
//     * @param id
//     * @param updateFileds 更新的字段名->字段值
//     */
//    public void updateDataById(String esIndex, String id, Map<String, Object> updateFileds) {
//        UpdateRequest request = new UpdateRequest(esIndex, id).doc(updateFileds);
//        try {
//            UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
//            if (response.status() == RestStatus.OK) {
//                System.out.println(String.format("更新索引为%s,id为%s的文档成功", response.getIndex(), response.getId()));
//            } else {
//                System.out.println(String.format("更新索引为%s,id为%s的文档失败", response.getIndex(), response.getId()));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 删除指定id的文档
//     *
//     * @param esIndex
//     * @param id
//     */
//    public void deleteDataById(String esIndex, String id) {
//        DeleteRequest request = new DeleteRequest(esIndex, id);
//        try {
//            DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
//            if (response.getResult() == DocWriteResponse.Result.DELETED) {
//                System.out.println(String.format("id为%s的文档删除成功", id));
//            } else {
//                System.out.println(String.format("id为%s的文档删除失败", id));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 批量插入
//     *
//     * @param esIndex
//     * @param datalist 数据集，数据格式为map<字段名,字段值>
//     */
//    public void bulkLoad(String esIndex, List<Map<String, Object>> datalist) {
//        BulkRequest bulkRequest = new BulkRequest();
//        for (Map<String, Object> data : datalist) {
//            Object id = data.get("id");
//            //如果数据包含id字段，使用数据id作为文档id
//            if (id != null) {
//                data.remove("id");
//                bulkRequest.add(new IndexRequest(esIndex).id(id.toString()).source(data));
//            } else {//让es自动生成id
//                bulkRequest.add(new IndexRequest(esIndex).source(data));
//            }
//        }
//        try {
//            BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
//            System.out.println(response.hasFailures());
//            if (!response.hasFailures()) {
//                System.out.println(String.format("索引%s批量插入成功，共插入%d条", esIndex, datalist.size()));
//            } else {
//                System.out.println(String.format("索引%s批量插入失败", esIndex));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public static void main(String[] args) {
//        //  ElasticsearchClient myEsUtils = new ElasticsearchClient();
//        Map<String, String> filedsMap = new HashMap<String, String>();
//        //filedsMap.put("area","Botswana");
//        //filedsMap.put("item","Roots, Other");
//        //filedsMap.put("indicatorname","Quantity");
////        BoolQueryBuilder queryBuilder = myEsUtils.getQueryBuilder(filedsMap);
////        List<String> glFields = new ArrayList<>();
////        glFields.add("name");
////        List<Map<String, Object>> list = myEsUtils.getPageResultList(queryBuilder, "nv_excel_pickup_origi", 1, 5, glFields);
////        System.out.println(list.size());
////        for (Map<String, Object> s : list) {
////            System.out.println(s);
////        }
////        System.out.println("count:" + myEsUtils.getResultCount(queryBuilder, "nv_excel_pickup_origi"));
//       /* HashMap<String, String> fileds = new HashMap<String, String>();
//        fileds.put("name","keyword");
//        fileds.put("age","long");
//        fileds.put("create_time","keyword");
//
//        createIndex("company3",1,0,fileds);*/
//        //deleteIndex("company3");
//       /* HashMap<String, Object> fileds = new HashMap<String, Object>();
//        fileds.put("age",25);
//        fileds.put("name","wangwu2");
//        fileds.put("@version",3);*/
//
//
//        //deleteDataById("company","4");
//      /*  ArrayList<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
//        HashMap<String, Object> data1 = new HashMap<String, Object>();
//        data1.put("id",5);
//        data1.put("name","zhangsan");
//        data1.put("age",18);
//        HashMap<String, Object> data2 = new HashMap<String, Object>();
//        data2.put("id",7);
//        data2.put("name","diao");
//        data2.put("age",22);
//        datas.add(data1);
//        datas.add(data2);
//        bulkLoad("company",datas);*/
//
//    }
//
//    public long deleteDataByQuery(String esIndex, QueryBuilder queryBuilder, int maxDocs) throws IOException {
//        long deletedCount = 0L;
//        DeleteByQueryRequest request = new DeleteByQueryRequest(esIndex);
//        request.setQuery(queryBuilder);
//        //最大设置10000
//        request.setBatchSize(10000);
//        //设置版本冲突时继续
//        request.setConflicts("proceed");
//        //最多处理文档数
//        request.setMaxDocs(maxDocs);
//        // 使用滚动参数来控制“搜索上下文”存活的时间
//        //request.setScroll(TimeValue.timeValueMinutes(10));
//        Long resultCount = getResultCount(queryBuilder, esIndex);
//        System.out.println(String.format("待删除数据%d条", resultCount));
//        int num = (int) Math.ceil((resultCount / (double) maxDocs));
//
//        for (int i = 1; i <= num; i++) {
//            BulkByScrollResponse bulkByScrollResponse = restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
//            deletedCount += bulkByScrollResponse.getDeleted();
//        }
//        return deletedCount;
//    }

}
