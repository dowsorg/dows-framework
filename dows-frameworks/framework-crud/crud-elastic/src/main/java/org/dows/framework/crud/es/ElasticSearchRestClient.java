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
            log.info("==index?????????==");
        }

    }

    public String getEsIdbyQuerys(String indexName, Map<String, Object> querys) {
        String id = "";
        SearchRequest request = new SearchRequest(indexName);
        //????????????
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
        log.info("????????????:{}", b);
        return b;
    }

    public void updateByQuery(String indexName, Map<String, Object> queryParams, Map<String, Object> updateScript) {
        UpdateByQueryRequest updateByQuery = new UpdateByQueryRequest(indexName);
        //??????????????????
        updateByQuery.setSlices(2);
        //?????????????????????????????????
        updateByQuery.setConflicts("proceed");
        //????????????????????????????????? ps???????????????????????????????????????????????????
        updateByQuery.setRefresh(true);
        //?????????????????????and????????????must ?????????or????????????should
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termQuery(key, value));
            updateByQuery.setQuery(boolQueryBuilder);
        }


        //?????????????????????????????????????????????????????????
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
//     * ??????????????????
//     *
//     * @param filedsMap ???????????? (key:???????????? ,vlues:???)
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
//     * ???????????????????????????
//     *
//     * @param queryBuilder ????????????
//     * @param esIndex      ?????????
//     * @param pageNo       ??????
//     * @param pagesize     ?????????
//     * @param glFields     ???????????????????????????
//     * @return
//     */
//    public List<Map<String, Object>> getPageResultList(QueryBuilder queryBuilder, String esIndex, int pageNo, int pagesize, List<String> glFields) {
//        SearchRequest searchRequest = new SearchRequest(esIndex);
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//
//        //??????????????????
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
//        // ???response???????????????
//        List<Map<String, Object>> list = new LinkedList();
//        searchResponse.getHits();
//
//        SearchHits hits = searchResponse.getHits();
//
//        Iterator<SearchHit> iterator = hits.iterator();
//        while (iterator.hasNext()) {
//            SearchHit next = iterator.next();
//            Map<String, Object> source = next.getSourceAsMap();
//            //??????????????????
//            Map<String, HighlightField> highlightFields = next.getHighlightFields();
//            for (String fieldName : glFields) {
//                HighlightField nameField = highlightFields.get(fieldName);
//                if (nameField != null) {
//                    Text[] fragments = nameField.fragments();
//                    StringBuilder nameTmp = new StringBuilder();
//                    for (Text text : fragments) {
//                        nameTmp.append(text);
//                    }
//                    //????????????????????????????????????
//                    source.put(fieldName, nameTmp.toString());
//                }
//            }
//            list.add(source);
//        }
//        return list;
//    }
//
//    /**
//     * ????????????
//     *
//     * @param query
//     * @return
//     */
//    public Map<String, Object> search(SearchRequestQuery query) throws Exception {
//        //???????????????
//
//        Map<String, Object> result = new HashMap<>();
//        List<Map<String, Object>> list = new ArrayList<>();
//        // 1?????????????????????
//        SearchRequest searchRequest = new SearchRequest(query.getEsIndex());
//        // 2???????????????
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        //3.????????????
//        int pageNo = 1, pageSize = 10;
//        if (query.getPageNo() != null) {
//            pageNo = query.getPageNo();
//        }
//        if (query.getPageSize() != null) {
//            pageSize = query.getPageSize();
//        }
//        //3.1 es????????????0?????????
//        sourceBuilder.from((pageNo - 1) * pageSize);
//        sourceBuilder.size(pageSize);
//        //4.???????????????????????????????????????????????????????????????????????????key??????and??????or??????not??????value????????????????????????
//        QueryBuilder queryBuilder = buildBasicQueryWithFilter(query);
//        sourceBuilder.query(queryBuilder);
//        //4.2 ????????????????????????1??????
//        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
//        // 5???????????????(???????????????????????????????????????????????????)
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        for (int i = 0; i < query.getKeywordFields().length; i++) {
//            highlightBuilder.field(query.getKeywordFields()[i]);
//        }
//        //5.1???????????????????????????????????????false???????????????????????????????????????????????????????????????????????????????????????
//        highlightBuilder.requireFieldMatch(true);
//        highlightBuilder.preTags("<span style='color:red'>");
//        highlightBuilder.postTags("</span>");
//        sourceBuilder.highlighter(highlightBuilder);
//        //6.????????????
//        String sortBy = query.getSortBy();
//        Boolean desc = query.getIsDesc();
//        if (StrUtil.isNotBlank(sortBy)) {
//            sourceBuilder.sort(new FieldSortBuilder(sortBy).order(desc ? SortOrder.DESC : SortOrder.ASC));
//        }
//        //7.??????????????????
//        Map<String, String> aggs = query.getAggMap();
//        if (aggs != null) {
//            for (Map.Entry<String, String> entry : aggs.entrySet()) {
//                //????????????(?????????
//                String aggName = entry.getKey();
//                //????????????
//                String aggFiled = entry.getValue();
//                if (aggName != null || aggFiled != null) {
//                    sourceBuilder.aggregation(AggregationBuilders.terms(aggName).field(aggFiled + ".keyword"));
//                }
//            }
//        }
//        //8?????????sourceFilter???????????????????????????,??????????????????????????????????????????????????????????????????????????????????????????null
//        sourceBuilder.fetchSource(query.getSourceFilter(), null);
//        //9???????????????
//        searchRequest.source(sourceBuilder);
//        try {
//            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//            for (SearchHit doc : searchResponse.getHits().getHits()) {
//                // ??????????????????
//                Map<String, HighlightField> highlightFields = doc.getHighlightFields();
//                for (int i = 0; i < query.getKeywordFields().length; i++) {
//                    HighlightField fieldTitle = highlightFields.get(query.getKeywordFields()[i]);
//                    // ????????????????????????
//                    Map<String, Object> sourceAsMap = doc.getSourceAsMap();
//                    if (fieldTitle != null) {
//                        // ??????????????????????????????
//                        Text[] fragments = fieldTitle.fragments();
//                        // ?????????????????????????????????
//                        String new_fieldTitle = "";
//                        for (Text res : fragments) {
//                            new_fieldTitle += res;
//                        }
//                        // ????????????????????????????????????
//                        sourceAsMap.put(query.getKeywordFields()[i], new_fieldTitle);
//                    }
//                    list.add(sourceAsMap);
//                }
//            }
//            // List ??????????????? ??????????????????????????????????????????????????????????????????????????????????????????
//            list = list.stream().distinct().collect(Collectors.toList());
//            int total = (int) searchResponse.getHits().getTotalHits().value;
//            result.put("data", list);
//            result.put("total", total);
//            result.put("totalPage", total == 0 ? 0 : (total % pageSize == 0 ? total / pageSize : (total / pageSize) + 1));
//            result.put("pageSize", pageSize);
//            result.put("pageNo", pageNo);
//
//            //??????????????????
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
//     * ??????????????????????????????
//     *
//     * @param aggregations
//     * @param query
//     * @return
//     */
//    private static List<Object> getAggData(Aggregations aggregations, SearchRequestQuery query) {
//        List<Object> result = new ArrayList<>();
//        for (Map.Entry<String, String> entry : query.getAggMap().entrySet()) {
//            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
//            //????????????(?????????
//            String aggName = entry.getKey();
//            //????????????
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
//        //????????????(and,or,not??????)
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
//        //?????????????????????????????????2019-07-01???2019-07-17
//        if (StrUtil.isNotBlank(query.getDateField()) || StrUtil.isNotBlank(query.getStartDate()) || StrUtil.isNotBlank(query.getEndDate())) {
//            queryBuilder.must(QueryBuilders.rangeQuery(query.getDateField()).from(query.getStartDate()).to(query.getEndDate()));
//        }
//        //?????????????????????????????????????????????????????????
//        if (query.getKeyword() == null || "".equals(query.getKeyword())) {
//            queryBuilder.must(QueryBuilders.matchAllQuery());
//            return queryBuilder;
//        }
//        if (flag == "or") {
//            //??????????????????????????????????????????????????????operator
//            queryBuilder.must(QueryBuilders.multiMatchQuery(query.getKeyword(), query.getKeywordFields()))
//                    //??????should???must?????????????????????
//                    .must(shouldQuery);
//        } else {
//            //?????????????????????????????????or?????????
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
//        // ???response???????????????
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
//     * ??????????????????
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
//     * ??????????????????
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
//     * ????????????????????????
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
//                System.out.println(String.format("??????%s?????????", esIndex));
//
//            } else {
//                System.out.println(String.format("??????%s?????????", esIndex));
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return isExists;
//    }
//
//    /**
//     * ????????????
//     *
//     * @param esIndex
//     * @param shards       ?????????
//     * @param replications ?????????
//     * @param fileds       ?????????->??????
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
//                    System.out.println(String.format("??????%s????????????", esIndex));
//                } else {
//                    System.out.println(String.format("??????%s????????????", esIndex));
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println(String.format("??????%s?????????", esIndex));
//        }
//    }
//
//    /**
//     * ????????????
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
//                System.out.println(String.format("??????%s?????????", esIndex));
//            } else {
//                System.out.println(String.format("??????%s????????????", esIndex));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * ??????id?????????????????????map(?????????,?????????)
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
//     * ????????????
//     *
//     * @param esIndex
//     * @param id
//     * @param updateFileds ??????????????????->?????????
//     */
//    public void updateDataById(String esIndex, String id, Map<String, Object> updateFileds) {
//        UpdateRequest request = new UpdateRequest(esIndex, id).doc(updateFileds);
//        try {
//            UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
//            if (response.status() == RestStatus.OK) {
//                System.out.println(String.format("???????????????%s,id???%s???????????????", response.getIndex(), response.getId()));
//            } else {
//                System.out.println(String.format("???????????????%s,id???%s???????????????", response.getIndex(), response.getId()));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * ????????????id?????????
//     *
//     * @param esIndex
//     * @param id
//     */
//    public void deleteDataById(String esIndex, String id) {
//        DeleteRequest request = new DeleteRequest(esIndex, id);
//        try {
//            DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
//            if (response.getResult() == DocWriteResponse.Result.DELETED) {
//                System.out.println(String.format("id???%s?????????????????????", id));
//            } else {
//                System.out.println(String.format("id???%s?????????????????????", id));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * ????????????
//     *
//     * @param esIndex
//     * @param datalist ???????????????????????????map<?????????,?????????>
//     */
//    public void bulkLoad(String esIndex, List<Map<String, Object>> datalist) {
//        BulkRequest bulkRequest = new BulkRequest();
//        for (Map<String, Object> data : datalist) {
//            Object id = data.get("id");
//            //??????????????????id?????????????????????id????????????id
//            if (id != null) {
//                data.remove("id");
//                bulkRequest.add(new IndexRequest(esIndex).id(id.toString()).source(data));
//            } else {//???es????????????id
//                bulkRequest.add(new IndexRequest(esIndex).source(data));
//            }
//        }
//        try {
//            BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
//            System.out.println(response.hasFailures());
//            if (!response.hasFailures()) {
//                System.out.println(String.format("??????%s??????????????????????????????%d???", esIndex, datalist.size()));
//            } else {
//                System.out.println(String.format("??????%s??????????????????", esIndex));
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
//        //????????????10000
//        request.setBatchSize(10000);
//        //???????????????????????????
//        request.setConflicts("proceed");
//        //?????????????????????
//        request.setMaxDocs(maxDocs);
//        // ???????????????????????????????????????????????????????????????
//        //request.setScroll(TimeValue.timeValueMinutes(10));
//        Long resultCount = getResultCount(queryBuilder, esIndex);
//        System.out.println(String.format("???????????????%d???", resultCount));
//        int num = (int) Math.ceil((resultCount / (double) maxDocs));
//
//        for (int i = 1; i <= num; i++) {
//            BulkByScrollResponse bulkByScrollResponse = restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
//            deletedCount += bulkByScrollResponse.getDeleted();
//        }
//        return deletedCount;
//    }

}
