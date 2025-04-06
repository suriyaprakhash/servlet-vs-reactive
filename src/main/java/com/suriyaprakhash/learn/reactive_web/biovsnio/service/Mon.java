//package com.suriyaprakhash.learn.reactive_web.biovsnio.controller;
//
//import java.lang.foreign.*;
//import java.lang.invoke.MethodHandle;
//
//public class Mon {
//
//    public static Integer print() {
//        Integer platformThreadId = null;
//        try (Arena arena = Arena.ofConfined()) {
//            SymbolLookup kernel32 = SymbolLookup.libraryLookup("kernel32.dll", arena);
//            MemorySegment getCurrentThreadIdSymbol = kernel32.find("GetCurrentThreadId").orElseThrow(() -> new RuntimeException("GetCurrentThreadId not found"));
//
//            FunctionDescriptor getCurrentThreadIdDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT);
//
//            Linker linker = Linker.nativeLinker();
//            MethodHandle getCurrentThreadIdHandle = linker.downcallHandle(getCurrentThreadIdSymbol, getCurrentThreadIdDescriptor);
//
//            platformThreadId = (int) getCurrentThreadIdHandle.invokeExact();
//
//            System.out.println("Platform Thread ID (GetCurrentThreadId): " + platformThreadId);
//
//        } catch (Throwable e) {
//            throw new RuntimeException(e);
//        }
//
//        return platformThreadId;
//    }
//}
