package com.softjake.blog.reactive.converter;

import com.softjake.blog.reactive.model.Member;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SyncToRxApi {


    String fetchMemberListAsString(String filename)
      throws NullPointerException, IOException {

        ClassLoader cl = getClass().getClassLoader();
        File file = new File(cl
          .getResource(filename)
          .getFile());
        String result = FileUtils.readFileToString(file,
          StandardCharsets.UTF_8);
        return result;
    }


    Flowable<Member> getMemberListAsFlowable(String filename) {
        return Single.<String>create(emitter -> {
            try {
                emitter.onSuccess(fetchMemberListAsString(filename));
            } catch (Exception e) {
                emitter.onError(e);
            }
        })
          .map(list -> new JsonArray(list))
          .flatMapPublisher(array -> Flowable.fromIterable(array))
          .cast(JsonObject.class)
          .map(json -> json.mapTo(Member.class));
    }


    Flowable<Member> getMemberListWithAntiPattern(String filename) {
        String members = "";
        try {
            members = fetchMemberListAsString(filename);
        } catch (Exception e) {
            System.out.println("Error on read: \n" + e.getMessage());
        }
        return Single.<String>just(members)
          .map(list -> new JsonArray(list))
          .flatMapPublisher(array -> Flowable.fromIterable(array))
          .cast(JsonObject.class)
          .map(json -> json.mapTo(Member.class));
    }
}
