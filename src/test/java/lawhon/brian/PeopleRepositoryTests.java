package lawhon.brian;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class PeopleRepositoryTests {

    @Test
    public void canSave() {
        PeopleRepository repo = new PeopleRepository();
        Person john = new Person("John", "Smith", ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6")));
        repo.save(john);
    }
}
