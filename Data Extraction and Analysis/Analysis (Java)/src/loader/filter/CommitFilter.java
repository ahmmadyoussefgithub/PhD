package loader.filter;
import model.Commit;


public interface CommitFilter {

	boolean filter(Commit commit);
}
