package hermes.ext.qpid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.apache.qpid.transport.codec.BBEncoder;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class QpidManagerTest {

    private static Context mockContext = mock(Context.class); 

    public static class TestInitialContextFactory 
        implements InitialContextFactory {

        @Override
        public Context getInitialContext(Hashtable<?, ?> environment)
                throws NamingException {

            return mockContext;
        }
        
        
    }

    @Test
    public final void testCreation() 
        throws Exception {

        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, TestInitialContextFactory.class.getName());

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        when(mockContext.lookup(QpidManager.HOST)).thenReturn(connectionFactory);
        
        Connection connection = mock(Connection.class);
        when(connectionFactory.createConnection()).thenReturn(connection);

        Session session = mock(Session.class);
        when(connection.createSession(anyBoolean(), anyInt())).thenReturn(session);

        new QpidManager(props);

        //тестируем правильную работу конструктора
        verify(connectionFactory).createConnection();
        verify(connection).start();
        verify(session).createProducer(any(Destination.class));
        verify(session).createQueue(anyString());
        verify(session).createConsumer(any(Destination.class));
    }

    @Test
    public final void testGetObject() 
        throws Exception {

        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, TestInitialContextFactory.class.getName());

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        when(mockContext.lookup(QpidManager.HOST)).thenReturn(connectionFactory);
        
        Connection connection = mock(Connection.class);
        when(connectionFactory.createConnection()).thenReturn(connection);

        Session session = mock(Session.class);
        when(connection.createSession(anyBoolean(), anyInt())).thenReturn(session);

        MessageProducer sender = mock(MessageProducer.class);
        when(session.createProducer(any(Destination.class))).thenReturn(sender);
        MessageConsumer receiver = mock(MessageConsumer.class);
        when(session.createConsumer(any(Destination.class))).thenReturn(receiver);

        MapMessage mapMessage = mock(MapMessage.class);
        when(session.createMapMessage()).thenReturn(mapMessage);

        BytesMessage bytesMessage = mock(BytesMessage.class);
        when(receiver.receive(anyLong())).thenReturn(bytesMessage);

        long capacity = 1024;
        List<Object> data = new ArrayList<Object>();
        data.add("abc");
        data.add("cde");

        BBEncoder bBEncoder = new BBEncoder((int)capacity);
        bBEncoder.writeList(data);
        
        final ByteBuffer buffer = bBEncoder.buffer();

        when(bytesMessage.getBodyLength()).thenReturn((long)buffer.array().length);
        when(bytesMessage.readBytes(any(byte[].class))).thenAnswer(new Answer<Integer>() {

            @Override
            public Integer answer(InvocationOnMock invocation) 
                throws Throwable {

                byte[] byteArray = (byte[]) invocation.getArguments()[0];
                System.arraycopy(buffer.array(), 0, byteArray, 0, byteArray.length);
                return byteArray.length;
            }
        });
        
        QpidManager manager = new QpidManager(props);
        List<Object> objects = manager.getObjects(QmfType.QUEUE);

        assertNotNull(objects);
        assertFalse(objects.isEmpty());
        assertEquals(data, objects);
    }

    @Test
    public final void testClose() 
        throws Exception {

        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, TestInitialContextFactory.class.getName());

        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        when(mockContext.lookup(QpidManager.HOST)).thenReturn(connectionFactory);
        
        Connection connection = mock(Connection.class);
        when(connectionFactory.createConnection()).thenReturn(connection);

        Session session = mock(Session.class);
        when(connection.createSession(anyBoolean(), anyInt())).thenReturn(session);
        
        QpidManager manager = new QpidManager(props);
        manager.close();

        verify(connection).close();
    }
}
