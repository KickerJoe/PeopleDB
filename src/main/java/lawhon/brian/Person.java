package lawhon.brian;

import java.time.ZonedDateTime;

public class Person {
    private Long id;

    public Person(String firstName, String lastName, ZonedDateTime dob) {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
