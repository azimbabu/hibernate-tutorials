import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import junit.framework.TestCase;
import tutorial.jpa.Event;

public class EntityManagerIllustrationTest extends TestCase {
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
    // create a couple of events
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    entityManager.persist(Event.builder().title("Our very first event!").date(new Date()).build());
    entityManager.persist(Event.builder().title("A follow up event").date(new Date()).build());
    entityManager.getTransaction().commit();
    entityManager.close();

    // now lets pull events from the database and list them
    entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
    List<Event> events = entityManager.createQuery("from Event", Event.class).getResultList();
    for (Event event : events) {
      System.out.println("Event=" + event);
    }
    entityManager.getTransaction().commit();
    entityManager.close();
  }
}
