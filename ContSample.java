import java.util.Optional;


public class ContSample {
	public static void main(String[] args) {
		CobolProgram program = new CobolProgram();
		try {
			program.runProgram();
		} catch (MyException e) {
		}
	}
}

class MyException extends Exception {}

class CobolProgram {
	private Cont[] contList = {
		new Cont(0) {
			public Optional<Cont> run() throws MyException {
				System.out.println("Start");
				Cont.performThrough(contList, 1, 2).run();
				Cont.perform(contList, 3).run();
				return Cont.goTo(contList[2]).run();
			}
		},
	
		new Cont(1) {
			public Optional<Cont> run() throws MyException {
				System.out.println("Perform Through 1");
				return Optional.of(contList[2]);
			}
		},

		new Cont(2) {
			public Optional<Cont> run() throws MyException {
				System.out.println("Perform Through 2");
				return Optional.of(contList[3]);
			}
		},

		new Cont(3) {
			public Optional<Cont> run() throws MyException {
				System.out.println("Perform 3");
				return Optional.of(contList[4]);
			}
		},

		Cont.pure()
	};
	
	public void runProgram() throws MyException {
		Optional<Cont> nextLabel = Optional.of(contList[0]);
		while(nextLabel.isPresent()) {
			Cont program = nextLabel.get();
			nextLabel = program.run();
		}
	}
}

abstract class Cont {
	abstract public Optional<Cont> run() throws MyException;
	public int contId = -1;
	public Cont() {
		this.contId = -1;
	}
	public Cont(int contId) {
		this.contId = contId;
	}

	static Cont pure() {
		return new Cont() {
			public Optional<Cont> run() throws MyException {
				return Optional.empty();
			}
		};
	}
	
	static Cont goTo(Cont cont) {
		return new Cont() {
			public Optional<Cont> run() throws MyException {
				return cont.run();
			}
		};
	}
	
	static Cont performThrough(Cont[] contList, int begin, int end) {
		return new Cont() {
			public Optional<Cont> run() throws MyException {
				Optional<Cont> nextCont = Optional.of(contList[begin]);
				int executedProgramId;
				do {
					Cont cont = nextCont.get();
					executedProgramId = cont.contId;
					nextCont = cont.run();
				} while(nextCont.isPresent() && executedProgramId != end);
				return Optional.of(Cont.pure());
			}
		};
	}

	static Cont perform(Cont[] contList, int labelId) {
		return Cont.performThrough(contList, labelId, labelId);
	}
}
