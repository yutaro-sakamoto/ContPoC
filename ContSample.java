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

class CobolProgram {
	private int count = 0;
	private Cont StartLabel = new Cont() {
		public Optional<Cont> run() throws MyException {
			System.out.println("Start");
			return Optional.of(LoopLabel);
		}
	};
	
	private Cont LoopLabel = new Cont() {
		public Optional<Cont> run() throws MyException {
			System.out.println("count = " + count);
			if(count >= 5) {
				return Optional.of(EndLabel);
			}
			count++;
			return Optional.of(LoopLabel);
		}
	};
	
	private Cont EndLabel = new Cont() {
		public Optional<Cont> run() throws MyException {
			System.out.println("End");
			return Optional.of(Cont.pure());
		}
	};
	
	public void runProgram() throws MyException {
		Optional<Cont> nextLabel = Optional.of(StartLabel);
		while(nextLabel.isPresent()) {
			Cont program = nextLabel.get();
			nextLabel = program.run();
		}
	}
}
/*
class CobolProgram2 {
	private int count = 0;
	private Cont[] contList = {
		new Cont() {
			public Optional<Cont> run() throws MyException {
				System.out.println("Start");
				return Optional.of(contList[1]);
			}
		},
	
		new Cont() {
			public Optional<Cont> run() throws MyException {
				System.out.println("count = " + count);
				if(count >= 5) {
					return Optional.of(contList[2]);
				}
				count++;
				return Optional.of(contList[1]);
			}
		},
			
		new Cont() {
			public Optional<Cont> run() throws MyException {
				System.out.println("End");
				Cont.performThrough(contList, 3, 5);
				throw new MyException();
				//return Optional.of(contList[3]);
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
}*/

class MyException extends Exception {
}

abstract class Cont {
	abstract public Optional<Cont> run() throws MyException;
	public int contId = -1;
	static Cont pure() {
		return new Cont() {
			public Optional<Cont> run() throws MyException {
				return Optional.empty();
			}
		};
	}
	
	static Cont go_to(Cont cont) {
		return new Cont() {
			public Optional<Cont> run() throws MyException {
				return cont.run();
			}
		};
	}
	
	/*static Cont performThrough(Cont[] contList, int begin, int end) {
		return new Cont() {
			public Optional<Cont> run() throws MyException {
				
			}
		}
	}*/
}
