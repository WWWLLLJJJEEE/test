package com.koitt.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;

// Advice 작성
public class GreetingAspect {
	
	public void start(JoinPoint point) {
		
		Signature signature = point.getSignature();
		String type = signature.getDeclaringTypeName();
		String name = signature.getName();
		Object[] args = point.getArgs();
		System.out.println(">> log start: " + type + " " + name + "()");
	}
	
	public void end() {
		System.out.println(">> log end");
	}
	
	// start, end를 동시에 구현하는 효과
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		
		System.out.println(">> around before");
		long startTime = System.nanoTime();
		
		// 해당 Method 파라메터 출력
		Object[] args = pjp.getArgs();
		for (Object item : args) {
			System.out.println(">> around args: " + item);
		}
		
		// 객체를 가로채서 멤버변수의 값을 변경할 수 있다.
		if (args != null && args.length > 0) {
			args[0] = "kim";
			args[1] = 19;
		}
		
		Object object = pjp.proceed(args);	// 원본 Method 실행
		
		long endTime = System.nanoTime();
		System.out.println(">> around after");
		System.out.println(">> Runtime: " + (endTime - startTime) + "ns");
		
		return "Return: Aspect Goodbye";
	}
	
	// Method가 return 수행할 때마다 호출
	public void returning() {
		System.out.println(">> returning");
	}
	
	// 예외가 발생할 때마다 호출
	public void throwing() {
		System.out.println(">> throwing");
	}
}
