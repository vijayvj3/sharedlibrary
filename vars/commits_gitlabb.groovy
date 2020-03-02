import groovy.json.*
import groovy.json.JsonSlurper 
//int ids1;

def call(jsondata){
      def jsonString = jsondata
      def jsonObj = readJSON text: jsonString
      String a=jsonObj.scm.projects.project.project_name
String Name=a.replaceAll("\\[", "").replaceAll("\\]","");
     withCredentials([usernamePassword(credentialsId: 'gitlab_cred', passwordVariable: 'password', usernameVariable:'username')]) {
      sh "curl -X GET    -u $username:$password https://gitlab.com/api/v4/users/5418155/projects -o output.json"
     }
   def jsonSlurper = new JsonSlurper()
 def reader = new BufferedReader(new InputStreamReader(new FileInputStream("/var/lib/jenkins/workspace/${JOB_NAME}/output.json"),"UTF-8"))
def resultJson = jsonSlurper.parse(reader)
def usertotal = resultJson.size()
      println(usertotal)
      println(Name)
      for(i=0;i<usertotal;i++)
         {
            if(Name==resultJson[i].name)
            {
               def id1 = resultJson[i].id 
               println(id1)
             return id1
            }
         }
         }
def commit(ids1,jsondata){
	def jsonString = jsondata
def jsonObj = readJSON text: jsonString
      println(ids1)
	int ecount = jsonObj.config.emails.email.size()
         println("No of users "+ ecount)
      withCredentials([usernamePassword(credentialsId: 'gitlab_cred', passwordVariable: 'password', usernameVariable:'username')]) {
	      sh "curl -X GET   -u $username:$password https://gitlab.com/api/v4/projects/${ids1}/repository/commits -o output.json"
      }
   def jsonSlurper = new JsonSlurper()
   def reader = new BufferedReader(new InputStreamReader(new FileInputStream("/var/lib/jenkins/workspace/${JOB_NAME}/output.json"),"UTF-8"))
def resultJson = jsonSlurper.parse(reader)
def total = resultJson.size()
   println(total)
	println(ecount)
      //println(JsonOutput.toJson(resultJson))
      List<String> JSON = new ArrayList<String>();
      List<String> JCOPY = new ArrayList<String>();
	 List<String> LIST = new ArrayList<String>();
def cnt=0
	 def jsonBuilder = new groovy.json.JsonBuilder()
for(i=0;i<ecount;i++)
 {
	def email=jsonObj.config.emails.email[i] 
  for(j=0;j<total;j++)
  {
	 // println(jsonObj.config.emails.email[i])
	 // println(resultJson[j].author_email)
   if(email==resultJson[j].author_email)
   {
	   JSON.add(JsonOutput.toJson(resultJson[j]))
	   cnt++
     }
     }
	 println(jsonObj.config.emails.email[i])
	 cnt=JSON.size()
	 LIST.add(["email":email,"Commit":JsonOutput.toJson(JSON),"Commit_cnt":cnt])
	   jsonBuilder(
	 
		  "individual":LIST
	   )
	 //JCOPY[i]=(JsonOutput.toJson(JSON))
	// println(JCOPY[i])
	 JSON.clear()
	 
	   
     }
  for(i=0;i<JCOPY.size();i++)
	{
		println(JCOPY[i])
	}
    
 jsonBuilder.gitlab(
  "commit" : resultJson,
  "commit_cnt" : resultJson.size()
  
  )
 println(jsonBuilder)
}
