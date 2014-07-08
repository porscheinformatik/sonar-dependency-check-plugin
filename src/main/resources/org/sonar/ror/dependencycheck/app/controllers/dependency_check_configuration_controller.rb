require 'rexml/document'

class DependencyCheckConfigurationController < ApplicationController
  SECTION=Navigation::SECTION_CONFIGURATION
  
  before_filter :admin_required

  #------------------GLOBAL-DEPENDENCIES-------------#  
    
  def index
    @allowedGlobalDependencies = REXML::Document.new Property.value('sonar.dependencycheck.lib.global')
    #render :template => 'dependency_check_configuration/index'
  end
  
  def add
    key=params['key']
    version_range=params['version_range']
    license=params['license']
    
newEntryAsString= <<EOF
        <dependency key='#{key}'>
            <versionRange>#{version_range}</versionRange>
            <license id='#{license}'/>
        </dependency>
EOF

    newEntry=REXML::Document.new(newEntryAsString)
    
    allowedProjectDependencies = REXML::Document.new Property.value('sonar.dependencycheck.lib.global') 
    allowedProjectDependencies.root.insert_before('//projectDependencies/dependency',newEntry.root)
    #allowedProjectDependencies.write($stdout, 2)
    Property.set('sonar.dependencycheck.lib.global',allowedProjectDependencies)
    
    redirect_to :action => 'index'
  end
  
  def delete 
    element=params['index'].to_i
          
    doc= REXML::Document.new Property.value('sonar.dependencycheck.lib.global')
    doc.root.elements["projectDependencies"].delete_element element
    #doc.write($stdout, 1)
    Property.set('sonar.dependencycheck.lib.global',doc)
    
    redirect_to :action => 'index'
  end
#------------------LICENSES--------------------------#
  def licenses
    
    @allowedLicenses = REXML::Document.new Property.value('sonar.dependencycheck.license')  
    render :template => 'dependency_check_configuration/licenses'
  end
  
  def addLicenses
    
    id=params['id']
    title=params['title']
    description=params['description']
    url=params['url'] 
    commercial=params['commercial']
    sourceType=params['sourceType']
      
newEntryAsString= <<EOF
        <license id='#{id}'>
            <title>#{title}</title>
            <description>#{description}</description>
            <url>#{url}</url>
            <sourceType>#{sourceType}</sourceType>
            <commercial>#{commercial}</commercial>
        </license>
EOF
   
    newEntry=REXML::Document.new(newEntryAsString)
      
      allowedLicenses = REXML::Document.new Property.value('sonar.dependencycheck.license') 
      allowedLicenses.root.insert_before('//licenses/license',newEntry.root)
      allowedLicenses.write($stdout, 2)
      Property.set('sonar.dependencycheck.license',allowedLicenses)
      
      redirect_to :action => 'licenses'
  end
  
  def deleteLicenses
    
    element=params['index'].to_i
          
    doc= REXML::Document.new Property.value('sonar.dependencycheck.license')
    doc.root.elements["licenses"].delete_element element
    doc.write($stdout, 1)
    Property.set('sonar.dependencycheck.license',doc)
    
    redirect_to :action => 'licenses'
  end
  #------------------PROJECT-DEPENDENCIES------------#
  def projectDependencies
    
    @allowedProjectDependencies = REXML::Document.new Property.value('sonar.dependencycheck.lib.project')   
        render :template => 'dependency_check_configuration/projectDependencies'
    
  end
  
  def addProjectDependencies
    
    key=params['key']
    version_range=params['version_range']
    license=params['license']
       
newEntryAsString= <<EOF
        <dependency key='#{key}'>
            <versionRange>#{version_range}</versionRange>
            <license id='#{license}'/>
        </dependency>
EOF
   
    newEntry=REXML::Document.new(newEntryAsString)
       
    allowedProjectDependencies = REXML::Document.new Property.value('sonar.dependencycheck.lib.project') 
    allowedProjectDependencies.root.insert_before('//projectDependencies/dependency',newEntry.root)
    #allowedProjectDependencies.write($stdout, 2)
    Property.set('sonar.dependencycheck.lib.project',allowedProjectDependencies)
       
    redirect_to :action => 'projectDependencies'
    
  end
  
  def deleteProjectDependencies
    
    element=params['index'].to_i
             
    doc= REXML::Document.new Property.value('sonar.dependencycheck.lib.project')
    doc.root.elements["projectDependencies"].delete_element element
    #doc.write($stdout, 1)
    Property.set('sonar.dependencycheck.lib.project',doc)
       
    redirect_to :action => 'projectDependencies'
    
  end
  #------------------SCOPE---------------------------#  
  def scope
    @scopeCompile = Property.value('dependencycheck.scope.compile')
    @scopeProvided = Property.value('dependencycheck.scope.provided')
    @scopeRuntime = Property.value('dependencycheck.scope.runtime')
    @scopeTest = Property.value('dependencycheck.scope.test')
    
    render :template => 'dependency_check_configuration/scope'
  end
  
  def saveScope
    
    compile=params['compile']
    provided=params['provided']
    runtime=params['runtime']
    test=params['test']
    
    Property.set('dependencycheck.scope.compile',compile)
    Property.set('dependencycheck.scope.provided',provided)
    Property.set('dependencycheck.scope.runtime',runtime)
    Property.set('dependencycheck.scope.test',test)
    
    redirect_to :action => 'scope'
  end
end