package com.martinadamek.jsonandroid;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface TestJson {

    String getName();

    List<Map> parsePublicTimeline(InputStream inputStream);

}
