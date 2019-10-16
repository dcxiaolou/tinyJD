package com.dcxiaolou.tinyJD.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dcxiaolou.tinyJD.bean.PmsSearchParam;
import com.dcxiaolou.tinyJD.bean.PmsSearchSkuInfo;
import com.dcxiaolou.tinyJD.bean.PmsSkuAttrValue;
import com.dcxiaolou.tinyJD.service.PmsSearchParamService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PmsSearchParamServiceImpl implements PmsSearchParamService {

    @Autowired
    private JestClient jestClient;

    @Override
    public List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam) {

        System.out.println("PmsSearchParamServiceImpl " + pmsSearchParam);
        List<PmsSearchSkuInfo> searchSkuInfos = new ArrayList<>();

        String s = getSearchDsl(pmsSearchParam);
        Search search = new Search.Builder(s).addIndex("gmall_pms").addType("PmsSkuInfo").build();
        SearchResult execute = null;
        try {
            execute = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo source = hit.source;

            Map<String, List<String>> highlight = hit.highlight;
            if (highlight != null) {
                String skuName = highlight.get("skuName").get(0);
                source.setSkuName(skuName);
            }
            searchSkuInfos.add(source);
        }

        return searchSkuInfos;
    }

    private String getSearchDsl(PmsSearchParam pmsSearchParam) {

        List<PmsSkuAttrValue> pmsSkuAttrValue = pmsSearchParam.getPmsSkuAttrValue();
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        if (StringUtils.isNotBlank(catalog3Id) && !"null".equals(catalog3Id)) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }

        if (pmsSkuAttrValue != null) {
            for (PmsSkuAttrValue skuAttrValue : pmsSkuAttrValue) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("pmsSkuAttrValue.valueId", skuAttrValue.getValueId());
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        if (StringUtils.isNotBlank(keyword)) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", keyword);
            boolQueryBuilder.must(matchQueryBuilder);
            matchQueryBuilder = new MatchQueryBuilder("skuDesc", keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }

        searchSourceBuilder.query(boolQueryBuilder);

        searchSourceBuilder.sort("productId", SortOrder.DESC);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color: red'>");
        highlightBuilder.field("skuName");
        highlightBuilder.postTags("</span>");

        searchSourceBuilder.highlight(highlightBuilder);

        searchSourceBuilder.from(0);

        searchSourceBuilder.size(20);

        String s = searchSourceBuilder.toString();

        System.out.println(s);

        return s;
    }
}
