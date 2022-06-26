package tutorial.jpa;

import java.util.Date;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "EVENTS")
public class Event {
  @Id
  @GeneratedValue(generator = "increment")
  @GenericGenerator(name = "increment", strategy = "increment")
  private Long id;

  private String title;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "EVENT_DATE")
  private Date date;
}
