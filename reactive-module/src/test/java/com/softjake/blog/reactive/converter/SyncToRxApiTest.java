package com.softjake.blog.reactive.converter;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.mockito.Matchers.isA;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FileUtils.class)
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
    public void testFetchMemberListAsString() throws IOException {
        //given
        String members = "";
        //when
        members = sample.fetchMemberListAsString("members.json");
        //then
        Assert.assertNotEquals("", members);
    }

    @Test(expected = NullPointerException.class)
    public void testFetchMemberListAsStringAndNPE() throws IOException {
        //given
        String members = "";
        //when
        members = sample.fetchMemberListAsString("badfile.json");
        //then
    }

    @Test(expected = IOException.class)
    public void testFetchMemberListAsStringAndIOE() throws IOException {
        PowerMockito.mockStatic(FileUtils.class);
        Mockito.when(
          FileUtils.readFileToString(isA(File.class), isA(Charset.class)))
          .thenThrow(new IOException("Testing for IOException"));
        //given
        String members = "";
        //when
        members = sample.fetchMemberListAsString("members.json");
        //then
        //test passes if IOException is thrown.
    }

    @Test
    public void testingGetMemberListAsFlowable() throws IOException {
        //given
        sample
          .getMemberListAsFlowable("members.json")
          //when
          .subscribe(member -> {
              //then
              Assert.assertNotEquals("Member last name is empty.",
                "", member.getLastName());
              System.out.println("From subscriber...\n" + member.toString());
          });
    }

    @Test
    public void testingGetMemberListWithAntiPattern() throws IOException {
        //given
        sample
          .getMemberListWithAntiPattern("members.json")
          //when
          .subscribe(member -> {
              //then
              Assert.assertNotEquals("Member last name is empty.",
                "", member.getLastName());
              System.out.println("From subscriber...\n" + member.toString());
          });
    }

}