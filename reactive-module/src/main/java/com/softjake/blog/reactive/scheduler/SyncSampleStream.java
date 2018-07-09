package com.softjake.blog.reactive.scheduler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softjake.blog.reactive.model.Member;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SyncSampleStream {


    @SuppressWarnings("Duplicates")
    Observable<String> getMemberListAsObservableString(File source) {
        return Observable.<String>create(emitter -> {
            try {
                String members = FileUtils.readFileToString(source,
                  StandardCharsets.UTF_8);
                emitter.onNext(members);
            } catch (Exception e) {
                emitter.onError(e);
            }
            emitter.onComplete();
        });
    }

    @SuppressWarnings("Duplicates")
    Observable<Member> getEachMemberAsObservableItem(File source)
      throws IOException {

        return Observable.<Member>create(emitter -> {
            try {
                ObjectMapper mapper = new ObjectMapper();   //---1---
                List<Member> list = mapper.readValue(source,
                  new TypeReference<List<Member>>() {});
                list.forEach(member -> {                    //---2---
                    printThread(
                      String.format("Emitter for %s = ",
                        member.getEmail()));
                    emitter.onNext(member);
                });
            } catch (IOException e) {
                printThread("Emitter for onError = ");
                emitter.onError(e);
                return;
            }
            printThread("Emitter for onComplete = ");
            emitter.onComplete();
        });
    }

    void printThread(String title) {
        System.out.println(String.format("%s thread: %s",
          title, Thread.currentThread().getName()));
    }

//    Observable<JsonArray> getMemberListAsObservableJsonArray() {}



    @SuppressWarnings("Duplicates")
    Observable<Member> getMemberListAntiPattern(File source) {
        String members = "";
        try {
            members = FileUtils.readFileToString(source, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("Error on read: \n" + e.getMessage());
        }
        return Single.<String>just(members)
          .map(list -> new JsonArray(list))
          .flatMapObservable(array -> Observable.fromIterable(array))
          .cast(JsonObject.class)
          .map(json -> json.mapTo(Member.class));
    }
}
