package com.softjake.blog.reactive.converter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class SyncToRxApiTest {

    private SyncToRxApi sample;

    @Before
    public void setUp() throws Exception {
        this.sample = new SyncToRxApi();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void loadSyncMemberListFileWithIOUtils() throws IOException {
        sample
          .getMemberListAsFlowable("members.json").subscribe(member -> {
            Assert.assertNotEquals("Member last name is empty.",
              "", member.getLastName());
            System.out.println("From subscriber...\n" + member.toString());
        });
    }

    @Test
    public void getMemberListWithAntiPattern() throws IOException {
        sample
          .getMemberListAntiPattern("members.json").subscribe(member -> {
            Assert.assertNotEquals("Member last name is empty.",
              "", member.getLastName());
            System.out.println("From subscriber...\n" + member.toString());
        });
    }

}