package org.spoofax.debug.core.control.java.events;

import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.event.LocatableEvent;


public class ValueExtractor extends InfoExtractorBase {

	private String varName;
	private String threadName;
	private int frameIndex;
	
	/**
	 * The string representation of the Value extracted from the other VM.
	 */
	private String stringValue;
	
	public ValueExtractor(LocatableEvent event, String threadName, int frameIndex, String varName) {
		this.event = event;
		this.threadName = threadName;
		this.frameIndex = frameIndex;
		this.varName = varName;
	}
	
	public String getStringValue() {
		return this.stringValue;
	}
	
	public ThreadReference getThread() {
		return this.event.thread();
	}
	
	public void extract() {
		// TODO: do some time profiling
		getStackFrame();
		getThisObject();
		
		@SuppressWarnings("unchecked")
		List<Method> methods = this.getThisObject().referenceType().methodsByName("getVariable");
		// method signature: Object getVariable(String threadName, int frameIndex, String varName)
		Method getVariableMethod = methods.get(0);
		Value objectValue = null;
		try {
			Value argThreadName = this.getThread().virtualMachine().mirrorOf(this.threadName);
			Value argFrameIndex = this.getThread().virtualMachine().mirrorOf(this.frameIndex);
			Value argVarName = this.getThread().virtualMachine().mirrorOf(this.varName);
			List<Value> arguments = new ArrayList<Value>();
			arguments.add(argThreadName);
			arguments.add(argFrameIndex);
			arguments.add(argVarName);
			objectValue = this.getStackFrame().thisObject().invokeMethod(getThread(), getVariableMethod, arguments, ThreadReference.INVOKE_SINGLE_THREADED);
			//this.extractor.updateContents();
		} catch (InvalidTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotLoadedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IncompatibleThreadStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (objectValue == null) {
			// failed
			System.out.println("NO VALUE RETURNED");
		} else {
			if (objectValue instanceof ObjectReference) {
				toString((ObjectReference) objectValue);
			}
		}
		
	}
	
	private void toString(ObjectReference ref) {
		@SuppressWarnings("unchecked")
		List<Method> methods = this.getThisObject().referenceType().methodsByName("toString");
		Method toStringMethod = methods.get(0);
		List<Value> arguments = new ArrayList<Value>();
		Value objectValue = null;
		try {
			objectValue = ref.invokeMethod(getThread(), toStringMethod, arguments, ThreadReference.INVOKE_SINGLE_THREADED);
		} catch (InvalidTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotLoadedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IncompatibleThreadStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.exception().toString());
		}
		
		if (objectValue == null) {
			System.out.println("TOSTRING FAILED");
		} else {
			if (objectValue instanceof StringReference) {
				String v = ((StringReference)objectValue).value();
				this.stringValue =v;
			}
		}
	}
}
