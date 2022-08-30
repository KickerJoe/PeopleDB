package lawhon.brian;

import java.sql.*;
import java.time.ZoneId;
import java.time.*;

public class PeopleRepository {
    private Connection connection;
    public PeopleRepository(Connection connection) {
        this.connection = connection;
    }

    public Person save(Person person) {
        String sql = "INSERT INTO PEOPLE (FIRST_NAME, LAST_NAME, DOB) VALUES(?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, person.getFirstName());
            ps.setString(2,person.getLastName());
            ps.setTimestamp(3, Timestamp.valueOf(person.getDob().withZoneSameInstant(ZoneId.of("+0")).toLocalDateTime()));
            int recordsAffected = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            while(rs.next()){
                long id = rs.getLong(1);
                person.setId(id);
            }
            System.out.printf("Records affected: %d%n", recordsAffected);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return person;
    }
}
