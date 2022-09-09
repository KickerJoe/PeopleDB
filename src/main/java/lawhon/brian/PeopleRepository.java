package lawhon.brian;

import java.math.BigDecimal;
import java.sql.*;
import java.time.ZoneId;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

public class PeopleRepository extends CRUDRepository<Person>{
    public static final String SAVE_PERSON_SQL = "INSERT INTO PEOPLE (FIRST_NAME, LAST_NAME, DOB) VALUES(?, ?, ?)";
    public static final String FIND_BY_ID_SQL = "SELECT ID, FIRST_NAME, LAST_NAME, DOB, SALARY FROM PEOPLE WHERE ID=?";
    public static final String FIND_ALL_SQL = "SELECT ID, FIRST_NAME, LAST_NAME, DOB, SALARY FROM PEOPLE";

    public PeopleRepository(Connection connection) {
        super(connection);
    }

    @Override
    String getSaveSql() {
        return SAVE_PERSON_SQL;
    }

    @Override
    String getFindByIdSql() {
        return FIND_BY_ID_SQL;
    }

    @Override
    void mapForSave(Person entity, PreparedStatement ps) throws SQLException {
        ps.setString(1, entity.getFirstName());
        ps.setString(2, entity.getLastName());
        ps.setTimestamp(3, convertDobToTimestamp(entity.getDob()));
    }

    @Override
    Person extractEntityFromResultSet(ResultSet rs) throws SQLException{
        long personId = rs.getLong("ID");
        String firstName = rs.getString("FIRST_NAME");
        String lastName = rs.getString("LAST_NAME");
        ZonedDateTime dob = ZonedDateTime.of(rs.getTimestamp("DOB").toLocalDateTime(), ZoneId.of("+0"));
        BigDecimal salary = rs.getBigDecimal("SALARY");
        return new Person(personId, firstName, lastName, dob, salary);
    }

    private static Timestamp convertDobToTimestamp(ZonedDateTime dob) {
        return Timestamp.valueOf(dob.withZoneSameInstant(ZoneId.of("+0")).toLocalDateTime());
    }

    public List<Person> findAll() {
        List<Person> people = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(FIND_ALL_SQL);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                people.add(extractEntityFromResultSet(rs));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return people;
    }

    public long count(){
        long count = 0;
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM PEOPLE");
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                count = rs.getLong(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return count;
    }

    public void delete(Person person) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM PEOPLE WHERE ID=?");
            ps.setLong(1, person.getId());
            int affectedRecordCount = ps.executeUpdate();
            System.out.println(affectedRecordCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Below method is bad programming, SQL injection. Avoid using a createStatement
    //Example purpose only. This will delete records all at the same time, instead of one by one
    public void delete(Person...people) {
        try {
            Statement stmt = connection.createStatement();
            String ids = Arrays.stream(people).map(Person::getId).map(String::valueOf).collect(joining(","));
            int affectedRecordCount = stmt.executeUpdate("DELETE FROM PEOPLE WHERE ID IN (:ids)".replace(":ids", ids));
            System.out.println(affectedRecordCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*
    The following code could replace the above. It will delete a person one at a time.
    This makes use of the delete method for removing one person.
        for (Person person : people){
            delete(person);
        }
     */

    public void update(Person person) {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE PEOPLE SET FIRST_NAME=?, LAST_NAME=?, DOB=?, SALARY=? WHERE ID=?");
            ps.setString(1, person.getFirstName());
            ps.setString(2, person.getLastName());
            ps.setTimestamp(3, convertDobToTimestamp(person.getDob()));
            ps.setBigDecimal(4, person.getSalary());
            ps.setLong(5, person.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
