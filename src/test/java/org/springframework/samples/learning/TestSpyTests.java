package org.springframework.samples.learning;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestSpyTests {

    // With a spy we can verify both state and behaviour
    @Spy
    List<String> listSpy = new ArrayList<String>();

    @Test
    public void testSpyReturnsRealValues() throws Exception {
        String s = "dobie";
        listSpy.add(new String(s));

        verify(listSpy).add(s);
        assertEquals(1, listSpy.size());
    }

    // With mocks only behaviour can be verified
    @Mock
    List<String> listMock = new ArrayList<String>();

    @Test
    public void testMockReturnsZero() throws Exception {
        String s = "dobie";

        listMock.add(new String(s));

        verify(listMock).add(s);
        assertEquals(0, listMock.size());
    }

    // With a spy we can also stub some calls, overwriting the functionality
    @Test
    public void testSpyReturnsStubbedValues() throws Exception {
        listSpy.add(new String("dobie"));
        assertEquals(1, listSpy.size());

        when(listSpy.get(anyInt())).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> listSpy.get(0));
    }

    @Test
    public void testSpyReturnsStubbedValues2() throws Exception {
        int size = 5;
        when(listSpy.size()).thenReturn(1, size);
         
        int mockedListSize = listSpy.size();
        assertEquals(1, mockedListSize);
         
        mockedListSize = listSpy.size();
        assertEquals(5, mockedListSize);  
       
        mockedListSize = listSpy.size();
        assertEquals(5, mockedListSize);  
       } 

}