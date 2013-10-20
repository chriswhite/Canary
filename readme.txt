Canary
Extended Non-Intrusive Trace Logging

    Canary represents the state of any variable to the application logs and standard output using a simple and non-intrusive syntax

    Canary traverses composite types such as arrays, lists and maps and represents each element or entry, including such entries which are themselves composite types, to infinite regress

    Canary includes the date and time, the name of the class, the name of the method and the line number of the calling code for enhanced debugging

    Canary is released under the GNU General Public License

Introduction
Problem Domain

Traditionally the key concern of logging has been the persistence of error messages and stack traces to log files for the purpose of diagnosis and remediation of faults and inconsistencies. However such error logging only provides information about the point at which the system failed and very often that information is insufficient to diagnose the root cause of the problem.

Trace logging provides feedback from components which are in proximity to such faulty components, in order to visualise the state of the system immediately before and around the error, and helps immensely in the diagnosis and remediation of otherwise potentially inscrutable problems.

The most basic form of trace logging involves printing feedback to the standard output stream:

System.out.println("Something went wrong");

In order to fully diagnose the problem it may be necessary to visualise the runtime state before the error occurred. Imagine there exists a list of numbers obtained from another component about which the faulty component makes certain assumptions:

public void doSomething(List<Integer> numbers) {
	Integer number = numbers.get(2);
}

Evidently this component assumes that the specified list will comprise three or more entries however in the event that the list only comprises two entries the doSomething method will raise an ArrayIndexOutOfBoundsException to the calling component.

In all likelihood the calling component will not catch that unchecked runtime exception and therefore the exception will escalate to the origin of the call hierarchy whereupon the thread will halt and the JVM will report the error message and stack trace associated with the exception.

The stack trace will identify the line of code comprised by the doSomething method which gave rise to the exception but will not provide any state information about the list of numbers which, being too short, ultimately caused the problem.

In order to diagnose the fault a developer may employ basic trace logging to probe the contents of the list:

public void doSomething(List<Integer> numbers) {

	for(Integer number: numbers){
		System.out.println("number: " + number);
	}

	Integer number = numbers.get(2);
}

That extra code will elucidate the contents of the list in the event of an error but the doSomething method now performs more work than necessary to complete its task. In practice a logging framework such as Log4j would be used to minimise the extra work required to employ trace logging:

public void doSomething(List<Integer> numbers) {

	if(logger.isTraceEnabled()){

		for(Integer number: numbers){
			logger.trace("number: " + number);
		}
	}

	Integer number = numbers.get(2);
}

The extra work is now only performed if trace logging is enabled. Traditionally trace logging is enabled only during development and disabled in production systems in order to maximise the performance of live applications.

In this case the extra code required for trace logging was not particularly extensive but nevertheless the doSomething method now contains three extra lines of functional code in order to probe the state of an object used by just one line of production code.

Very often the extra code required for trace logging exceeds the amount of associated production code and operations which contain many lines of trace logging code; sometimes fairly complex logging code which probes the state of deep composites of objects, can become exponentially difficult to read and maintain.

Enter Canary

Canary reduces the amount of trace logging code to a bare minimum. Reviewing the doSomething method using Canary instead of Log4j:

public void doSomething(List<Integer> numbers) {

	Canary.output("numbers", numbers);

	Integer number = numbers.get(2);
}

Canary will only perform the work required to iterate over the entries of the numbers list if trace logging is enabled and then will output the specified name of the variable, in this case the name 'numbers', along with the date and time, class name and method and line number of the calling code, and of course the entire contents of the numbers list, to the standard output stream and/or the application logs depending on the configuration of Canary outlined in the next section.

Working Example

Create a properties file in the root of the java source hierarchy alongside log4j.properties named canary.properties with the following contents in order to activate Canary during development:

canary.logLevel=trace
canary.writeToApplicationLogs=true
canary.writeToStandardOutput=true
canary.maximumRepresentationCharacters=200

In the absence of any canary.properties file Canary will by default perform no work and output nothing to the application logs or standard output.

Create a list of fruit to begin the working example:

List<String> fruit = new LinkedList<String>();
fruit.add("apple");
fruit.add("banana");
fruit.add("orange");
fruit.add("pear");

// prints
// 2012/11/11 14:31:21.164 Example.main.27: fruit: (apple, banana, orange, pear)
Canary.output("fruit", fruit);

Create a list of vegetables:

List<String> vegetables = new LinkedList<String>();
vegetables.add("tomato");
vegetables.add("potato");
vegetables.add("carrot");
vegetables.add("onion");

Create a map of food and add entries for fruit and vegetables:

Map<String, List> food = new HashMap<String, List>();
food.put("fruit", fruit);
food.put("vegetables", vegetables);

// prints (using one line)
// 2012/11/11 14:31:21.165 Example.main.48: food: {fruit => (apple, banana, orange, pear),
// vegetables => (tomato, potato, carrot, onion)}
Canary.output("food", food);

Convert the lists of fruit and vegetables to arrays:

String[] fruitArray = fruit.toArray(new String[4]);
String[] vegetableArray = vegetables.toArray(new String[4]);

Add the fruit and vegetables arrays to a containing array:

String[][] foodArray = new String[][]{fruitArray, vegetableArray};

// prints
// 2012/11/11 14:31:21.166 Example.main.59: foodArray: [[apple, banana, orange, pear], [tomato, potato, carrot, onion]]
Canary.output("foodArray", foodArray);

Canary will output the value of any variable type including bespoke objects for which the return value of the toString method will be represented:

// prints (using one line truncated at 200 characters as specified in the canary.properties file)
// 2012/11/11 14:31:21.167 Example.main.66: now: java.util.GregorianCalendar[time=1352645262486,areFieldsSet=true,
// areAllFieldsSet=true,lenient=true,zone=sun.util.calendar.ZoneInfo[id="Europe/England",offset=0,dstSavings=3600000,
// useDaylight=...
Canary.output("now", new GregorianCalendar());

Canary will also output the value of any primitive type:

// prints
// 2012/11/11 14:31:21.168 Example.main.71: number: 256
Canary.output("number", 256L);

Canary will also output arbitrary remarks without any corresponding object:

// prints
// 2012/11/11 14:31:21.169 Example.main.75: jack and jill went up the hill
Canary.output("jack and jill went up the hill");

Remove the canary.properties file from the java source/class hierarchy in order to disable Canary and thereby maximise performance in production environments.

© 2010-2013 Chris White. All rights reserved
