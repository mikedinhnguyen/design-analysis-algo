/*
 * JOCL - Java bindings for OpenCL
 * 
 * Copyright 2009 Marco Hutter - http://www.jocl.org/
 */

import static org.jocl.CL.*;
import org.jocl.*;
import java.io.*;
import java.util.*;

public class JoclMaxProd
{
    /**
     * The source code of the OpenCL program to execute
     */
    private static String programSource =
        "__kernel void "+
        "arrayKernel(__constant const float *a,"+
        "             __constant const float *b,"+
        "             __global float *c,"+
        "             __global float *d)"+
        "{"+
        "    int gid = get_global_id(0);"+
        "    c[gid] = a[gid] * b[gid];"+
        "    d[gid] = gid;"+
        "    int i = gid-1;"+
        "    if (gid != 0)"+
        "    {"+
        "        barrier(CLK_GLOBAL_MEM_FENCE);"+
        "        if (c[gid] < c[i])"+
        "        {"+
        "            c[gid] = c[i];"+
        "            d[gid] = d[i];"+
        "        }"+
        "    }"+
        "}";
    
    public static void main(String args[])
    {
        String filename = args[0];
        ArrayList<String>arr = new ArrayList<>();
        try {
            File file = new File(filename + ".txt");
            FileReader fr = new FileReader(file);

            BufferedReader br = new BufferedReader(fr);
            String line;
            
            while ((line = br.readLine()) != null) {
                arr.add(line);
            }
            fr.close();            
        } catch (IOException e) {
            System.out.println("Could not find file: " + filename + ".txt");
            e.printStackTrace();
        }

        // Create input- and output data 
        int n = arr.size();
        float srcArrayA[] = new float[n];
        float srcArrayB[] = new float[n];
        float dstArray[] = new float[n];
        float indexArray[] = new float[n];
        for (int i=0; i<n; i++)
        {
            String[] split = arr.get(i).split(",");        
            srcArrayA[i] = Integer.parseInt(split[0]);
            srcArrayB[i] = Integer.parseInt(split[1]);
        }
        dstArray[0] = -1;
        dstArray[1] = Float.MIN_VALUE;
        Pointer srcA = Pointer.to(srcArrayA);
        Pointer srcB = Pointer.to(srcArrayB);
        Pointer dst = Pointer.to(dstArray);
        Pointer ind =Pointer.to(indexArray);

        // The platform, device type and device number
        // that will be used
        final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);
        
        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];
        
        // Obtain a device ID 
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        cl_context context = clCreateContext(
            contextProperties, 1, new cl_device_id[]{device}, 
            null, null, null);
        
        /* Create a command-queue for the selected device
           This syntax is not deprecated in OpenCL 2 in favor of
           cl_queue_properties cmd_props = new cl_queue_properties();
           int[] errors = new int[1];
           cl_command_queue commandQueue =
               clCreateCommandQueueWithProperties(context, device, cmd_props,
               errors);
            However, the new syntax requires native compilation I have not done
         */
        cl_command_queue commandQueue = 
            clCreateCommandQueue(context, device, 0, null);

        // Allocate the memory objects for the input- and output data
        cl_mem memObjects[] = new cl_mem[4];
        memObjects[0] = clCreateBuffer(context, 
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            Sizeof.cl_float * n, srcA, null);
        memObjects[1] = clCreateBuffer(context, 
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            Sizeof.cl_float * n, srcB, null);
        memObjects[2] = clCreateBuffer(context, 
            CL_MEM_READ_WRITE, 
            Sizeof.cl_float * n, null, null);
        memObjects[3] = clCreateBuffer(context, 
            CL_MEM_READ_WRITE, 
            Sizeof.cl_float * n, null, null);
        
        // Create the program from the source code
        cl_program program = clCreateProgramWithSource(context,
            1, new String[]{ programSource }, null, null);
        
        // long startTime = System.nanoTime();
        // Build the program
        clBuildProgram(program, 0, null, null, null, null);
        
        // Create the kernel
        cl_kernel kernel = clCreateKernel(program, "arrayKernel", null);
        
        // Set the arguments for the kernel
        clSetKernelArg(kernel, 0, 
            Sizeof.cl_mem, Pointer.to(memObjects[0]));
        clSetKernelArg(kernel, 1, 
            Sizeof.cl_mem, Pointer.to(memObjects[1]));
        clSetKernelArg(kernel, 2, 
            Sizeof.cl_mem, Pointer.to(memObjects[2]));
        clSetKernelArg(kernel, 3, 
            Sizeof.cl_mem, Pointer.to(memObjects[3]));
        
        // Set the work-item dimensions
        long global_work_size[] = new long[]{n};
        long local_work_size[] = new long[]{1};
        
        // Execute the kernel
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
            global_work_size, local_work_size, 0, null, null);
        
        // Read the output data
        clEnqueueReadBuffer(commandQueue, memObjects[2], CL_TRUE, 0,
            n * Sizeof.cl_float, dst, 0, null, null);
        clEnqueueReadBuffer(commandQueue, memObjects[3], CL_TRUE, 0,
            n * Sizeof.cl_float, ind, 0, null, null);
        
        // Release kernel, program, and memory objects
        clReleaseMemObject(memObjects[0]);
        clReleaseMemObject(memObjects[1]);
        clReleaseMemObject(memObjects[2]);
        clReleaseMemObject(memObjects[3]);
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);
        
        // long endTime = System.nanoTime();
        // long duration = (endTime - startTime);
        // System.out.println("Runtime: " + duration/1000000 + " ms");

        // Verify the result
        // int highestNum = Integer.MIN_VALUE;
        // int index = 0;
        // for (int i = 0;i<n;i++)
        // {
        //     highestNum = Math.max(highestNum, (int)dstArray[i]);
        //     index = highestNum == dstArray[i] ? i : index;
        // }
        // System.out.println(index +","+highestNum);

        System.out.println((int)indexArray[n-1]+","+(int)dstArray[n-1]);
    }
}

// javac -classpath "./jocl-2.0.4.jar" JoclMaxProd.java
// java -classpath ".:./jocl-2.0.4.jar" JoclMaxProd file