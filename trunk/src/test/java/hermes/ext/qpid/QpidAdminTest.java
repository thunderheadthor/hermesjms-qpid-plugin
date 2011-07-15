package hermes.ext.qpid;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import hermes.Hermes;
import hermes.config.DestinationConfig;
import hermes.ext.qpid.QmfTypes;
import hermes.ext.qpid.QpidAdmin;
import hermes.ext.qpid.QpidManager;
import hermes.ext.qpid.qmf.QMFObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;



public class QpidAdminTest {

    /**
     * @throws Exception
     */
    @Test
    public final void testGetDepth()
        throws Exception {

        final String queueName = "MyQueue";
        final long depthExpected = 12345;

        final Hermes hermes = mock(Hermes.class);
        final QpidManager qpidManager = mock(QpidManager.class);
        QpidAdmin qpidAdmin = new QpidAdmin(hermes, qpidManager);

        final List<Map<String, ?>> data = createFakeData(queueName, depthExpected);
        when(qpidManager.<Map<String, ?>>getObjects(QmfTypes.QUEUE)).thenReturn(data);

        final DestinationConfig destinationConfig = mock(DestinationConfig.class);
        when(destinationConfig.getName()).thenReturn(queueName);
        final int depth = qpidAdmin.getDepth(destinationConfig);

        assertEquals(depthExpected, depth);
    }

    private List<Map<String, ?>> createFakeData(final String queueName, final long depthExpected) {

        final List<Map<String, ?>> data = new ArrayList<Map<String, ?>>();
        final Map<String, Object> row = new HashMap<String, Object>();
        final Map<String, Object> queueInfo = new HashMap<String, Object>();
        queueInfo.put(QMFObject.NAME, queueName.getBytes());
        queueInfo.put(QMFObject.MSG_DEPTH, depthExpected);
        row.put(QMFObject._VALUES, queueInfo);
        data.add(row);
        return data;
    }

    @Test
    public final void testClose()
        throws Exception {

        final Hermes hermes = mock(Hermes.class);
        final QpidManager qpidManager = mock(QpidManager.class);
        QpidAdmin qpidAdmin = new QpidAdmin(hermes, qpidManager);
        qpidAdmin.close();
        verify(qpidManager).close();
    }

    @Test
    public final void testDiscoverDestinationConfigs()
        throws Exception {

        final Hermes hermes = mock(Hermes.class);
        final QpidManager qpidManager = mock(QpidManager.class);

        QpidAdmin qpidAdmin = new QpidAdmin(hermes, qpidManager);

        final String queueName = "testDiscoverDestinationConfigsQueue";
        final long depthExpected = 10;
        final List<Map<String, ?>> data = createFakeData(queueName, depthExpected);
        when(qpidManager.<Map<String, ?>>getObjects(QmfTypes.QUEUE)).thenReturn(data);

        final QpidAdmin qpidAdminSpy = spy(qpidAdmin);
        final DestinationConfig destinationConfig = new DestinationConfig();
        doReturn(destinationConfig).when(qpidAdminSpy).buildDestinationConfig(queueName);

        final Collection<DestinationConfig> discoverDestinationConfigs
            = qpidAdminSpy.discoverDestinationConfigs();

        assertEquals(data.size(), discoverDestinationConfigs.size());
    }

    @Test
    public final void testGetStatistics()
        throws Exception {

        final Hermes hermes = mock(Hermes.class);
        final QpidManager qpidManager = mock(QpidManager.class);
        String queueName = "testGetStatisticsQueue";
        long depthExpected = 15;
        final List<Map<String, ?>> data = createFakeData(queueName, depthExpected);
        when(qpidManager.<Map<String, ?>>getObjects(QmfTypes.QUEUE)).thenReturn(data);

        QpidAdmin qpidAdmin = new QpidAdmin(hermes, qpidManager);
        final DestinationConfig destinationConfig = mock(DestinationConfig.class);
        when(destinationConfig.getName()).thenReturn(queueName);
        final Map<String, Object> actual = qpidAdmin.getStatistics(destinationConfig);

        final Map<String, String> expected = new HashMap<String, String>();
        expected.put(QMFObject.NAME, queueName);
        expected.put(QMFObject.MSG_DEPTH, "" + depthExpected);
        assertEquals(expected, actual);
    }
}
