package com.softjake.blog.reactive.scheduler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softjake.blog.reactive.model.Member;
import io.reactivex.Observable;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SyncSampleStream {

    void printThread(String title) {
        System.out.println(String.format("%s thread: %s",
          title, Thread.currentThread().getName()));
    }

    File getFileObject(String filename) {
        ClassLoader cl = getClass().getClassLoader();
        return new File(cl.getResource(filename).getFile());
    }

    String fetchFileAsString(String filename) throws IOException {
        printThread(String.format("Fetch %s from", filename));
        return FileUtils.readFileToString(getFileObject(filename),
          StandardCharsets.UTF_8);
    }


    Observable<String> getMemberListAsObservableString(String filename) throws InterruptedException {
        printThread(String.format("Source name: %s", filename));
        Observable<String> observable = Observable.<String>create(emitter -> {
            try {
                String content = fetchFileAsString(filename);
                emitter.onNext(content);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                emitter.onError(e);
            }
            emitter.onComplete();
        });
        return observable;
    }

    Observable<Member> getEachMemberAsObservableItem(File source)
      throws IOException {
        printThread(String.format("Fetching %s from", source.getName()));
        return Observable.<Member>create(emitter -> {
            try {
                ObjectMapper mapper = new ObjectMapper();   //---1---
                List<Member> list = mapper.readValue(source,
                  new TypeReference<List<Member>>() {
                  });
                list.forEach(member -> {                    //---2---
                    printThread(
                      String.format("Emitter for %s = ",
                        member.getEmail()));
                    emitter.onNext(member);
                });
            } catch (IOException e) {
                printThread("Emitter for onError =");
                emitter.onError(e);
                return;
            }
            printThread("Emitter for onComplete =");
            emitter.onComplete();
        });
    }

}
