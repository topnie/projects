package webapp.repositories;

import org.springframework.stereotype.Repository;
import webapp.bank.model.wniosek.WniosekZmiana;

import java.util.List;

@Repository
public interface IChangeInfoRequestRepository {

    List<WniosekZmiana> getAll();

    void addWniosek(WniosekZmiana w);

    void deleteWniosek(long w_id);

    long getId();

    void acceptWniosek(long id);
}
