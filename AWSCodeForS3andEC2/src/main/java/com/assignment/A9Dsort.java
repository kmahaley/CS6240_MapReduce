package com.assignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.opencsv.CSVParser;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class A9Dsort {
	static AmazonEC2 ec2;
	static AmazonS3 s3;
	static AWSCredentials credentials = null;
	static List<String> activeInstances = new LinkedList<>();
	static List<String> activednsInstances = new LinkedList<>();
	static CSVParser csv_parser = new CSVParser();
	static int numberOfNodes;
	static String ami = "ami-08111162";
	static String tempGroup = "JavaSecurityGroup";
	static String bucketName = "";
	static String pemKeys = "";
	static String secretKey = "";
	static String accessKey = "";
	static String instanceTypeByUser="";
	/**
	 * Mention your credentials of aws here either you can have credentials at
	 * /.aws/credentials or mention manually using BasicAWSCredentials
	 * 
	 * @throws Exception
	 */
	private static void startUp() throws Exception {
//		try {
//			credentials = new ProfileCredentialsProvider().getCredentials();
//		} catch (Exception e) {
//			throw new AmazonClientException("location (~/.aws/credentials), and is in valid format.", e);
//		}
		s3 = new AmazonS3Client(new BasicAWSCredentials(secretKey, accessKey));
		ec2 = new AmazonEC2Client(new BasicAWSCredentials(secretKey, accessKey));
	}

	/**
	 * This function takes input as "start/copy/run/stop, number of instance, secrete key, access key, region, bucket name, 
	 * instance type" this will trigger start instances, upload files to the instances and stop instances 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String[] input = { "stop", "3", "secretkey****", "accessKey*****", "us-east-1",
				"awsBucketName","t2.micro" };
		args = input;
		secretKey = args[2];
		accessKey = args[3];
		pemKeys = args[4];
		bucketName = args[5];
		instanceTypeByUser=args[6];
		startUp();
		if (args[0].toLowerCase().equals("start")) {
			numberOfNodes = Integer.valueOf(args[1]);
			createInstances();
			writeIPsIntoS3Bucket();
		} else if (args[0].toLowerCase().equals("copy")) {
			readIPsFromS3Bucket(args[0]);
			copyJarToInstances();
		} else if (args[0].toLowerCase().equals("run")) {
			runJarInInstances();
		} else if (args[0].toLowerCase().equals("stop")) {
			readIPsFromS3Bucket(args[0]);
			terminateInstances();
			deleteSecurityGroup();
		} else {
			System.out.println("usage: [action(start OR stop)] [number of nodes]");
			System.exit(-1);
		}
	}
	/**
	 * 
	 */
	private static void runJarInInstances() {
		try{
			S3Object s3o = s3.getObject(new GetObjectRequest(bucketName, "ipaddress.txt"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(s3o.getObjectContent()));
			String line = null;
			String[] instances;
			String user = "ec2-user";
			int port = 22;
			String privateKey = "/completePath/keys.pem";
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				instances = csv_parser.parseLine(line);
				if (!instances[0].equals("0")) {
					JSch jsch = new JSch();
					String host = instances[2];
					jsch.addIdentity(privateKey);
					System.out.println("identity added ");
					Session session = jsch.getSession(user, host, port);
					System.out.println("session created.");
					java.util.Properties config = new java.util.Properties();
					config.put("StrictHostKeyChecking", "no");
					session.setConfig(config);
					session.connect();
					Channel channel = session.openChannel("shell");

					InputStream in = new FileInputStream(new File("command.txt"));
					channel.setInputStream(in);
					channel.setOutputStream(System.out);
					channel.connect(10 * 1000);
				}
			}
			System.out.println("All tasks are finished!");
			}catch (Exception e) {
				System.out.println(e);
			}
		
	}

	/**
	 * Copy jar and properties file from given location to EC2 instance /tmp 
	 * location
	 */
	private static void copyJarToInstances() {
		try {
			for (String dns : activednsInstances) {
				ProcessBuilder pb1 = new ProcessBuilder("scp", "-i", "/completePath/key.pem", "-o",
						"StrictHostKeyChecking=no", "/completePath/test.jar", "ec2-user@" + dns + ":/tmp");
				Process p1 = null;
				int error1 = -1;
				do {
					p1 = pb1.start();
					error1 = p1.waitFor();
				} while (error1 != 0);

				System.out.print(dns+"\tJar  = " + error1+"\t");
				if (error1 == 0) {
					ProcessBuilder pb2 = new ProcessBuilder("scp", "-i", "/completePath/us-east-1.pem", "-o",
							"StrictHostKeyChecking=no", "/completePath/log4j.properties",
							"ec2-user@" + dns + ":/tmp");
					Process p2 = null;
					int error2 = -1;
					do {
						p2 = pb2.start();
						error2 = p2.waitFor();
					} while (error2 != 0);
					System.out.println("Config file  = " + error2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Creates instances and along with the security group. Start instances
	 * 
	 * @throws InterruptedException
	 */
	private static void createInstances() throws InterruptedException {

		CreateSecurityGroupRequest r1 = new CreateSecurityGroupRequest(tempGroup, "A9 Security group");
		ec2.createSecurityGroup(r1);
		AuthorizeSecurityGroupIngressRequest r2 = new AuthorizeSecurityGroupIngressRequest();
		r2.setGroupName(tempGroup);

		/************* the property of http *****************/
		IpPermission permission = new IpPermission();
		permission.setIpProtocol("tcp");
		permission.setFromPort(80);
		permission.setToPort(80);
		List<String> ipRanges = new ArrayList<String>();
		ipRanges.add("0.0.0.0/0");
		permission.setIpRanges(ipRanges);

		/************* the property of SSH **********************/
		IpPermission permission1 = new IpPermission();
		permission1.setIpProtocol("tcp");
		permission1.setFromPort(22);
		permission1.setToPort(22);
		List<String> ipRanges1 = new ArrayList<String>();
		ipRanges1.add("0.0.0.0/0");
		permission1.setIpRanges(ipRanges1);

		/************* the property of https **********************/
		IpPermission permission2 = new IpPermission();
		permission2.setIpProtocol("tcp");
		permission2.setFromPort(443);
		permission2.setToPort(443);
		List<String> ipRanges2 = new ArrayList<String>();
		ipRanges2.add("0.0.0.0/0");
		permission2.setIpRanges(ipRanges2);

		/************* the property of tcp **********************/
		IpPermission permission3 = new IpPermission();
		permission3.setIpProtocol("tcp");
		permission3.setFromPort(0);
		permission3.setToPort(65535);
		List<String> ipRanges3 = new ArrayList<String>();
		ipRanges3.add("0.0.0.0/0");
		permission3.setIpRanges(ipRanges3);

		/********************** add rules to the group *********************/
		List<IpPermission> permissions = new ArrayList<IpPermission>();
		permissions.add(permission);
		permissions.add(permission1);
		permissions.add(permission2);
		permissions.add(permission3);
		r2.setIpPermissions(permissions);

		ec2.authorizeSecurityGroupIngress(r2);
		List<String> groupName = new ArrayList<String>();
		groupName.add(tempGroup);

		System.out.println("Number of intances = " + numberOfNodes);
		String imageId = ami;
		int minInstanceCount = 1;
		int maxInstanceCount = numberOfNodes;
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest(imageId, minInstanceCount, maxInstanceCount);
		runInstancesRequest.setInstanceType(instanceTypeByUser);
		runInstancesRequest.setKeyName(pemKeys);
		runInstancesRequest.setSecurityGroups(groupName);
		RunInstancesResult runInstancesResult = ec2.runInstances(runInstancesRequest);

		/* To make sure instances are running */
		System.out.println("wait for instances to be in running state");
		List<Instance> resultInstance = runInstancesResult.getReservation().getInstances();
		for (Instance ins : resultInstance) {
			while(getInstanceStatus(ins.getInstanceId()) != 16){
				Thread.sleep(5000);
			}
			activeInstances.add(ins.getInstanceId());
		}
		System.out.println("Instances created and running");
	}

	/**
	 * Function receives instanceID as input and returns the integer code of the instance state
	 * @param instanceId
	 * @return state code of the instance
	 */
	public static Integer getInstanceStatus(String instanceId) {
	    DescribeInstancesRequest describeInstanceRequest = new DescribeInstancesRequest().withInstanceIds(instanceId);
	    DescribeInstancesResult describeInstanceResult = ec2.describeInstances(describeInstanceRequest);
	    InstanceState state = describeInstanceResult.getReservations().get(0).getInstances().get(0).getState();
	    return state.getCode();
	}
	
	/**
	 * stops created instances
	 */
	private static void stopInstances() {
		StopInstancesRequest stop = new StopInstancesRequest(activeInstances);
		ec2.stopInstances(stop);
	}

	/**
	 * terminates created instances
	 */
	private static void terminateInstances() {
		TerminateInstancesRequest terminate = new TerminateInstancesRequest(activeInstances);
		ec2.terminateInstances(terminate);
	}

	/**
	 * This function sleeps and wait for instances to get terminated and then
	 * delete security group
	 * 
	 * @throws InterruptedException
	 */
	private static void deleteSecurityGroup() throws InterruptedException {
		for (String s : activeInstances) {
			while(getInstanceStatus(s) != 48){
				Thread.sleep(5000);
			}
			System.out.println(s+" = terminated");
		}
		DeleteSecurityGroupRequest d = new DeleteSecurityGroupRequest().withGroupName(tempGroup);
		ec2.deleteSecurityGroup(d);
		System.out.println("Group Deleted");
	}

	/**
	 * Writing IP address to the s3 bucket with helper function "createIPFile"
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void writeIPsIntoS3Bucket() throws IOException {
		s3.putObject(new PutObjectRequest(bucketName, "ipaddress.txt", createIPFile()));
	}

	private static File createIPFile() throws IOException {
		File file = File.createTempFile("ipaddress", ".txt");
		file.deleteOnExit();
		Writer writer = new OutputStreamWriter(new FileOutputStream(file));
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		request.setInstanceIds(activeInstances);
		DescribeInstancesResult res = ec2.describeInstances(request);
		List<Reservation> reservations = res.getReservations();
		List<Instance> instances;
		int count = 0;
		for (Reservation r : reservations) {
			instances = r.getInstances();
			for (Instance ins : instances) {
				System.out.println(ins.getPublicIpAddress()+","+ins.getPublicDnsName());
				writer.write(count + "," + ins.getInstanceId() + "," + ins.getPublicIpAddress() + ","+ ins.getPublicDnsName() + "\n");
				count++;
			}
		}
		writer.close();
		return file;
	}

	/**
	 * Read IP addressess file from s3 bucket and populates the activeInstances
	 * with instance ID
	 * @param String which specifies either add dnsname or ipname to list
	 * @throws IOException
	 */
	private static void readIPsFromS3Bucket(String action) throws IOException {
		S3Object s3o = s3.getObject(new GetObjectRequest(bucketName, "ipaddress.txt"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(s3o.getObjectContent()));
		String line = null;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			String[] instanceIDs = csv_parser.parseLine(line);
			if (action.equalsIgnoreCase("stop"))
				activeInstances.add(instanceIDs[1]);
			else
				activednsInstances.add(instanceIDs[3]);
		}
	}
	/**
	 * Create folder and write into it in aws s3 bucket
	 */
	private static void createFolderAndWriteFileS3() throws IOException{
        String folder = "folder/";
        String fileName=folder+"output-000";
        deleteFolder(folder);
        List<Integer> list=Arrays.asList(1,2,3,4);
        File file=File.createTempFile(fileName,"");
        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        for (Integer integer : list) {
			writer.write(integer.toString());
		}
        writer.close();
        s3.putObject(new PutObjectRequest(bucketName, fileName, file));
	}
	/**
	 * @param foldername as string
	 * @return boolean if folder exist in aws s3 bucket 
	 */
	private static boolean folderExists(String folder) {
		ObjectListing objs = s3.listObjects(new ListObjectsRequest().withBucketName(bucketName)
				.withPrefix(folder));
		return objs.getObjectSummaries() != null && !objs.getObjectSummaries().isEmpty();
	}

	/**
	 * delete folder from aws s3
	 * @param folder name as string
	 * @return boolean value if folder is deleted or not
	 */
	private static boolean deleteFolder(String folder) {
		if (!folderExists(folder)) return false;
		List<S3ObjectSummary> l = s3.listObjects(bucketName, folder).getObjectSummaries();
		for (S3ObjectSummary f : l)
			s3.deleteObject(bucketName, f.getKey());
		s3.deleteObject(bucketName, folder);
		return true;
	}
	
	/**
	 * Read a file from aws S3 bucket
	 */
	private static void readFromS3() {
		String key = "199607hourly.txt.gz";
		S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
		System.out.println(object.getKey());
		InputStream is = object.getObjectContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line;
		try {
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create S3 bucket and it can also delete S3 bucket
	 */
	private static void s3CreateBucket() {
		String bucketname = "BucketName";
		bucketname = bucketname.toLowerCase();
		s3.createBucket(bucketname);
		// s3.deleteBucket(bucketname);
	}

	/**
	 * Below functions are taken from github for reference
	 */
	private static void s3Buckets() {
		/*
		 * Amazon S3
		 *
		 * The AWS S3 client allows you to manage buckets and programmatically
		 * put and get objects to those buckets.
		 *
		 * In this sample, we use an S3 client to iterate over all the buckets
		 * owned by the current user, and all the object metadata in each
		 * bucket, to obtain a total object and space usage count. This is done
		 * without ever actually downloading a single object -- the requests
		 * work with object metadata only.
		 */
		try {
			List<Bucket> buckets = s3.listBuckets();

			long totalSize = 0;
			int totalItems = 0;
			for (Bucket bucket : buckets) {
				/*
				 * In order to save bandwidth, an S3 object listing does not
				 * contain every object in the bucket; after a certain point the
				 * S3ObjectListing is truncated, and further pages must be
				 * obtained with the AmazonS3Client.listNextBatchOfObjects()
				 * method.
				 */
				System.out.println(bucket.getName());
				ObjectListing objects = s3.listObjects(bucket.getName());
				do {
					for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
						totalSize += objectSummary.getSize();
						totalItems++;
					}
					objects = s3.listNextBatchOfObjects(objects);
				} while (objects.isTruncated());
			}

			System.out.println("You have " + buckets.size() + " Amazon S3 bucket(s), " + "containing " + totalItems
					+ " objects with a total size of " + totalSize + " bytes.");
		} catch (AmazonServiceException ase) {
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Error Message: " + ace.getMessage());
		}

	}

	/**
	 * list of all ec2 instances with their status
	 */
	private static void ec2Cluster() {
		/*
		 * Amazon EC2
		 *
		 * The AWS EC2 client allows you to create, delete, and administer
		 * instances programmatically.
		 *
		 * In this sample, we use an EC2 client to get a list of all the
		 * availability zones, and all instances sorted by reservation id.
		 */
		try {
			DescribeAvailabilityZonesResult availabilityZonesResult = ec2.describeAvailabilityZones();
			System.out.println("You have access to " + availabilityZonesResult.getAvailabilityZones().size()
					+ " Availability Zones.");

			DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
			List<Reservation> reservations = describeInstancesRequest.getReservations();
			Set<Instance> instances = new HashSet<Instance>();

			for (Reservation reservation : reservations) {
				instances.addAll(reservation.getInstances());
			}

			System.out.println("You have " + instances.size() + " Amazon EC2 instance(s) running.");
		} catch (AmazonServiceException ase) {
			System.out.println("Caught Exception: " + ase.getMessage());
			System.out.println("Reponse Status Code: " + ase.getStatusCode());
			System.out.println("Error Code: " + ase.getErrorCode());
			System.out.println("Request ID: " + ase.getRequestId());
		}

	}
}