require 'rexml/document'

class DependencycheckConfigurationController < ApplicationController
  SECTION = Navigation::SECTION_CONFIGURATION
  before_filter :admin_required

  def load_licenses
    REXML::Document.new Property.value('sonar.dependencycheck.license')
  end

  #------------------GLOBAL-DEPENDENCIES-------------#  

  def index
    @allowedGlobalDependencies = REXML::Document.new Property.value('sonar.dependencycheck.lib.global')
    @licenseIds = []
    load_licenses().elements.each('/licenses/license') do |license|
      @licenseIds.push license.elements['id'].text
    end
  end

  def add
    key=params['key']
    version_range=params['version_range']
    license=params['license']
    newEntryAsString= <<EOF
    <dependency>
        <key>#{key}</key>
        <versionRange>#{version_range}</versionRange>
        <licenseId>#{license}</licenseId>
    </dependency>
EOF
    allowedProjectDependencies = REXML::Document.new Property.value('sonar.dependencycheck.lib.global') 
    allowedProjectDependencies.root << REXML::Document.new(newEntryAsString).root

    Property.set('sonar.dependencycheck.lib.global', allowedProjectDependencies)

    redirect_to :action => 'index'
  end
  
  def delete
    element=params['index'].to_i

    doc = REXML::Document.new Property.value('sonar.dependencycheck.lib.global')
    doc.root.delete_element element
    Property.set('sonar.dependencycheck.lib.global',doc)

    redirect_to :action => 'index'
  end

  def licenses
    @allowedLicenses = REXML::Document.new Property.value('sonar.dependencycheck.license')
    render :template => 'dependencycheck_configuration/licenses'
  end

  def addLicenses
    
    id = params['id']
    title = params['title']
    description = params['description']
    url = params['url']
    commercial = params['commercial']
    sourceType = params['sourceType']

    newEntryAsString= <<EOF
    <license>
        <id>#{id}</id>
        <title>#{title}</title>
        <description><![CDATA[#{description}]]></description>
        <url>#{url}</url>
        <sourceType>#{sourceType}</sourceType>
        <commercial>#{commercial}</commercial>
    </license>
EOF

    allowedLicenses = REXML::Document.new Property.value('sonar.dependencycheck.license')
    if allowedLicenses.root.elements["/licenses/license[id='#{id}']"]
        @error = "License with id '#{id}' already exists."
        licenses
    else
        allowedLicenses.root << REXML::Document.new(newEntryAsString).root
        Property.set('sonar.dependencycheck.license', allowedLicenses)
        redirect_to :action => 'licenses'
    end
  end

  def deleteLicense
    licenseId = params['id'].to_s

    libs = REXML::Document.new Property.value('sonar.dependencycheck.lib.global')
    if libs.root.elements["/allowed-dependencies/dependency[licenseId='#{licenseId}']"]
        @error = "License '#{licenseId}' cannot be deleted because dependencies are using it.";
        licenses
    else
        doc = REXML::Document.new Property.value('sonar.dependencycheck.license')
        doc.root.delete_element "/licenses/license[id = '#{licenseId}']"
        Property.set('sonar.dependencycheck.license', doc)

        redirect_to :action => 'licenses'
    end
  end

end