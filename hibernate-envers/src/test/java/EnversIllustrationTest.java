import java.util.Date;
import java.util.List;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import junit.framework.TestCase;
import tutorial.jpa.Event;

public class EnversIllustrationTest extends TestCase {
  private EntityManagerFactory entityManagerFactory;

  @Override
  protected void setUp() throws Exception {
    entityManagerFactory = Persistence.createEntityManagerFactory("tutorial.jpa");
  }

  @Override
  protected void tearDown() throws Exception {
    if (entityManagerFactory != null) {
      entityManagerFactory.close();
    }
  }

  public void testBasicUsage() {
    // create a couple of events...
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    entityManager.persist(Event.builder().title("Our very first event!").date(new Date()).build());
    entityManager.persist(Event.builder().title("A follow up event").date(new Date()).build());
    entityManager.getTransaction().commit();
    entityManager.close();

    // now lets pull events from the database and list them
    entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    List<Event> result = entityManager.createQuery("from Event", Event.class).getResultList();
    for (Event event : result) {
      System.out.println("Event " + event);
    }
    entityManager.getTransaction().commit();
    entityManager.close();

    // so far the code is the same as we have seen in previous tutorials.  Now lets leverage
    // Envers...

    // first lets create some revisions
    entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    Event myEvent =
        entityManager.find(
            Event.class, 2L); // we are using the increment generator, so we know 2 is a valid id
    myEvent.setDate(new Date());
    myEvent.setTitle(myEvent.getTitle() + " (rescheduled)");
    entityManager.getTransaction().commit();
    entityManager.close();

    // and then use an AuditReader to look back through history
    entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();

    myEvent = entityManager.find(Event.class, 2L);
    assertEquals("A follow up event (rescheduled)", myEvent.getTitle());

    AuditReader auditReader = AuditReaderFactory.get(entityManager);
    Event firstRevision = auditReader.find(Event.class, 2L, 1);
    assertFalse(firstRevision.getTitle().equals(myEvent.getTitle()));
    assertFalse(firstRevision.getDate().equals(myEvent.getDate()));

    Event secondRevision = auditReader.find(Event.class, 2L, 2);
    assertTrue(secondRevision.getTitle().equals(myEvent.getTitle()));
    assertTrue(secondRevision.getDate().equals(myEvent.getDate()));

    entityManager.getTransaction().commit();
    entityManager.close();
  }
}
