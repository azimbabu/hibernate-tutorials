import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import junit.framework.TestCase;
import tutorial.hbm.Event;

public class AnnotationsIllustrationTest extends TestCase {
  private SessionFactory sessionFactory;

  @Override
  protected void setUp() throws Exception {
    // A SessionFactory is set up once for an application!
    final StandardServiceRegistry registry =
        new StandardServiceRegistryBuilder()
            .configure() // configures settings from hibernate.cfg.xml
            .build();
    try {
      sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    } catch (Exception ex) {
      // The registry would be destroyed by the SessionFactory, but we had trouble building the
      // SessionFactory. So destroy it manually.
      StandardServiceRegistryBuilder.destroy(registry);
    }
  }

  @Override
  protected void tearDown() throws Exception {
    if (sessionFactory != null) {
      sessionFactory.close();
    }
  }

  public void testBasicUsage() {
    // create a couple of events...
    try (Session session = sessionFactory.openSession()) {
      session.beginTransaction();
      session.persist(Event.builder().title("Our very first event!").date(new Date()).build());
      session.persist(Event.builder().title("A follow up event").date(new Date()).build());
      session.getTransaction().commit();
    }

    // now lets pull events from the database and list them
    try (Session session = sessionFactory.openSession()) {
      session.beginTransaction();
      List<Event> events = session.createQuery("from Event").list();
      for (Event event : events) {
        System.out.println("Event=" + event);
      }
      session.getTransaction().commit();
    }
  }
}
