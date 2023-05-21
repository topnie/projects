package webapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.bank.model.wniosek.WniosekZmiana;
import webapp.repositories.ChangeInfoRequestRepository;

import java.util.List;

@Service
public class ChangeInfoRequestService {

    @Autowired
    private ChangeInfoRequestRepository changeInfoRequestRepository;

    public List<WniosekZmiana> getAlL() {
        return changeInfoRequestRepository.getAll();
    }

    public void addWniosek(WniosekZmiana w) {
        changeInfoRequestRepository.addWniosek(w);
    }

    public void deleteWniosek(long w_id) {
        changeInfoRequestRepository.deleteWniosek(w_id);
    }

    public long getNewId() {
        return changeInfoRequestRepository.getId();
    }

    public void acceptWniosek(long w_id) {
        changeInfoRequestRepository.acceptWniosek(w_id);
    }
}
