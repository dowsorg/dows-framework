package org.dows.framework.crud.es;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lait.zhang@gmail.com
 * @description: TODO
 * @weixin SH330786
 * @date 4/28/2022
 */
@Component
public class ElasticClient {

    @Autowired
    private RestHighLevelClient esClient;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * ???????????????
     *
     * @param indexName
     * @return
     * @throws IOException
     */
    public boolean createIndex(String indexName) throws IOException {

        CreateIndexRequest request = new CreateIndexRequest(indexName);
        CreateIndexResponse response = esClient.indices().create(request, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }

    /**
     * ?????????????????? ElasticsearchRestTemplate ????????????indexName ?????????????????? index
     *
     * @param index
     * @param indexName
     */
    public void indexCreate(Class index, String indexName) {
        if (null == index || StrUtil.isBlank(indexName)) {
            return;
        }
        IndexCoordinates indexCoordinates = IndexCoordinates.of(indexName);
        if (!elasticsearchRestTemplate.indexOps(indexCoordinates).exists()) {
            // ???????????????????????????mapping??????
            Document mapping = elasticsearchRestTemplate.indexOps(indexCoordinates).createMapping(index);
            // ????????????
            // ????????????mapping
            elasticsearchRestTemplate.indexOps(indexCoordinates).putMapping(mapping);
        }
    }


    /**
     * ????????????????????????
     *
     * @param indexName
     * @return
     * @throws IOException
     */
    public boolean existIndex(String indexName) throws IOException {
        GetIndexRequest request = new GetIndexRequest(indexName);
        return esClient.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * ???????????????
     *
     * @param indexName
     * @throws IOException
     */
    public boolean deleteHotelIndex(String indexName) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        boolean delFlag = false;
        // 1.??????Request??????
        GetIndexRequest request1 = new GetIndexRequest(indexName);
        // 2.????????????
        boolean exists = esClient.indices().exists(request1, RequestOptions.DEFAULT);
        // 3.??????
        System.err.println(exists ? "????????????????????????" : "?????????????????????");
        if (exists) {
            AcknowledgedResponse response = esClient.indices().delete(request, RequestOptions.DEFAULT);
            delFlag = response.isAcknowledged();
            System.out.println("????????????");
        }
        return delFlag;
    }

    /**
     * ??????????????????
     *
     * @param indexName
     * @param json
     * @param id
     * @throws IOException
     */
    public void addDocument(String indexName, String json, String id) throws IOException {
        // 1.??????Request??????
        IndexRequest request = new IndexRequest(indexName).id(id);
        // 2.??????Json??????
        request.source(json, XContentType.JSON);
        // 3.????????????
        IndexResponse response = esClient.index(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }

    /**
     * ??????????????????
     *
     * @param list
     * @param indexName
     * @param id
     * @throws IOException
     */
    public void testBulkDocument(List<String> list, String indexName, String id) throws IOException {
        // 1.??????Request
        BulkRequest request = new BulkRequest();
        // 2.????????????
        for (String json : list) {
            request.add(new IndexRequest(indexName)
                    .id(id)
                    .source(json, XContentType.JSON));
        }
        // 3.????????????
        BulkResponse bulk = esClient.bulk(request, RequestOptions.DEFAULT);
        System.out.println(bulk.status());// ok
    }

    /**
     * ??????????????????
     *
     * @param indexName
     * @param json
     * @param id
     * @throws IOException
     */
    public void updateDocument(String indexName, String json, String id) throws IOException {
        // 1.??????Request
        UpdateRequest request = new UpdateRequest(indexName, id);
        // 2.??????????????????
        request.doc(json, XContentType.JSON);
        // 3.????????????
        UpdateResponse response = esClient.update(request, RequestOptions.DEFAULT);
        System.out.println(response.status()); // OK
    }

    /**
     * ??????id ????????????
     *
     * @param indexName
     * @param id
     * @return
     * @throws IOException
     */
    public String getDocumentById(String indexName, String id) throws IOException {
        // 1.??????Request
        GetRequest request = new GetRequest(indexName, id);
        // 2.???????????????????????????
        GetResponse response = esClient.get(request, RequestOptions.DEFAULT);
        // 3.??????????????????
        String json = response.getSourceAsString();
        return json;
    }

    /**
     * ??????id????????????
     *
     * @param indexName
     * @param id
     * @throws IOException
     */
    public void deleteDocument(String indexName, String id) throws IOException {
        // 1.??????Request
        DeleteRequest request = new DeleteRequest(indexName, id);
        // 2.????????????
        DeleteResponse response = esClient.delete(request, RequestOptions.DEFAULT);
        System.out.println(response.status());// OK
    }

    /**
     * ????????????
     *
     * @param indexName
     * @return
     * @throws IOException
     */
    public SearchHit[] matchAll(String indexName) throws IOException {
        // 1 ??????request??????
        SearchRequest request = new SearchRequest(indexName);
        // 2 ???????????? ???DSL??????
        request.source().query(QueryBuilders.matchAllQuery());
        // 3.????????????
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        // 4. ??????
        SearchHits searchHits = response.getHits();
        // ???????????????
        long total = searchHits.getTotalHits().value;
        // ??????????????????
        SearchHit[] hits = searchHits.getHits();
        return hits;
    }

    /**
     * ??????????????????????????????
     *
     * @param indexName
     * @param name
     * @param text
     * @return
     * @throws IOException
     */
    public SearchHit[] match(String indexName, String name, String text) throws IOException {
        // 1 ??????request??????
        SearchRequest request = new SearchRequest(indexName);
        // 2 ???????????? ???DSL??????
        request.source().query(QueryBuilders.matchQuery(name, text));
        // 3.????????????
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        // 4. ??????
        SearchHits searchHits = response.getHits();
        // ???????????????
        long total = searchHits.getTotalHits().value;
        // ??????????????????
        SearchHit[] hits = searchHits.getHits();
        return hits;
    }

    /**
     * ??????????????????
     *
     * @param indexName
     * @param pageNum
     * @param pageSize
     * @return
     * @throws IOException
     */
    public SearchHit[] testPageSort(String indexName, int pageNum, int pageSize) throws IOException {
        // 1 ??????request??????
        SearchRequest request = new SearchRequest(indexName);
        // 2 ???????????? ???DSL??????
        request.source().query(QueryBuilders.matchAllQuery());
        // ??????
//        request.source().sort("price", SortOrder.ASC);
        request.source().from((pageNum - 1) * 5).size(pageSize);
        // 3.????????????
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        // 4. ??????
        SearchHits searchHits = response.getHits();
        // ???????????????
        long total = searchHits.getTotalHits().value;
        // ??????????????????
        SearchHit[] hits = searchHits.getHits();
        return hits;
    }

    /**
     * ????????????
     *
     * @param indexName
     * @param name
     * @param text
     * @return
     * @throws IOException
     */
    public SearchHit[] testHighLight(String indexName, String name, String text) throws IOException {
        // 1 ??????request??????
        SearchRequest request = new SearchRequest(indexName);
        // 2 ???????????? ???DSL??????
        request.source().query(QueryBuilders.matchQuery(name, text));
        request.source().highlighter(new HighlightBuilder().field("name").requireFieldMatch(false));
        // 3.????????????
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        // 4. ??????
        SearchHits searchHits = response.getHits();
        // ???????????????
        long total = searchHits.getTotalHits().value;
        // ??????????????????
        SearchHit[] hits = searchHits.getHits();
        return hits;
    }


    /**
     * ??????????????????????????????
     *
     * @param response
     * @param tclass
     * @param <T>
     * @return
     */
    private <T/* extends */> List<T> handleResponse(SearchResponse response, Class<T> tclass) {
        SearchHits searchHits = response.getHits();
        // ???????????????
        long total = searchHits.getTotalHits().value;
        // ??????????????????
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            // ??????json
            String json = searchHit.getSourceAsString();
            // json ?????????
            T t = JSONUtil.toBean(json, tclass);
            // ??????????????????
            Map<String, HighlightField> highlightFieldMap = searchHit.getHighlightFields();
            if (!CollectionUtils.isEmpty(highlightFieldMap)) {
                // ?????????????????????????????????
                HighlightField highlightField = highlightFieldMap.get("name");
                if (highlightField != null) {
                    // ??????????????????
                    String name = highlightField.getFragments()[0].toString();
                    // ??????????????????
                    //t.setName(name);
                }
            }
            System.out.println(t);
        }
        return null;
    }

    /**
     * springframework.data.elasticsearch ??????elasticsearchRestTemplate??????
     *
     * @param c
     * @param page
     * @param size
     * @param index
     * @param boolQueryBuilder
     * @param sort
     * @param <T>
     * @return
     */
    public <T> List<T> getList(Class<T> c, Integer page, Integer size, String index, BoolQueryBuilder boolQueryBuilder, String... sort) {
        if (ObjectUtil.isEmpty(page)) {
            page = 0;
        }
        if (ObjectUtil.isEmpty(size)) {
            size = 1000;
        }
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(boolQueryBuilder);
        PageRequest pageRequest = PageRequest.of(page, size);
        if (ObjectUtil.isNotEmpty(sort)) {
            pageRequest.withSort(Sort.by(Sort.Direction.DESC, sort));
        }
        nativeSearchQuery.setPageable(pageRequest);
        nativeSearchQuery.setTrackTotalHits(true);
        org.springframework.data.elasticsearch.core.SearchHits<T> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, c, IndexCoordinates.of(index));
        List<org.springframework.data.elasticsearch.core.SearchHit<T>> searchHit = searchHits.getSearchHits();
        List<T> returnResult = searchHit.stream().map(org.springframework.data.elasticsearch.core.SearchHit::getContent).collect(Collectors.toList());
        return returnResult;
    }

    public Long getTotalHitNum(String index, BoolQueryBuilder boolQueryBuilder) {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.indices(index);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
            return response.getHits().getTotalHits().value;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * sum ????????????
     *
     * @return
     */
    public List<Terms.Bucket> sumAggregationData(String indices, QueryBuilder queryBuilder, TermsAggregationBuilder sumBuilder, String gropuName) {
        List<Terms.Bucket> buckets = new ArrayList<>();
        try {
            SearchRequest searchRequest = new SearchRequest();
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.aggregation(sumBuilder);
            searchSourceBuilder.query(queryBuilder);
            searchRequest.indices(indices);
            searchRequest.source(searchSourceBuilder);
            SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
            //????????????
            Aggregations aggregations = response.getAggregations();
            //??????????????????????????? .???null??????????????????????????????Terms
            Terms terms = aggregations.get(gropuName);
            buckets = (List<Terms.Bucket>) terms.getBuckets();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buckets;
    }


}
