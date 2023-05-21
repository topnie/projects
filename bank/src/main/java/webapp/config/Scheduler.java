package webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import webapp.repositories.LoanRepository;

@Service
public class Scheduler {

    @Autowired
    private LoanRepository loanRepository;

    @Scheduled(cron = "0 0 1 * * ?")
    public void scheduleTaskAtMidnight() {
        System.out.println("Scheduler working...");
        loanRepository.updateAllDates();
    }


}
