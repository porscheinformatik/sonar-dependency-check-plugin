class Api::ExportController < Api::ApiController

    def to_csv
      
      project_sid = Project.find(params[:id]).last_snapshot.project_snapshot.id
        
      scopes = ['PRJ']  
      
  #<!--Unterscheidet bzgl des Ladens der Dependencies, ob das gesamte Projekt oder ein bestimmtes Modul ausgewaehlt ist-->
        
     if Project.find(params[:id]).qualifier =='TRK'
      dependencies=Dependency.find(:all, :include => ['to','to_snapshot'], :order => "projects.kee")
    else
        dependencies = Dependency.find(:all, :include => ['to','to_snapshot'], :conditions => ["project_snapshot_id=? and (from_scope in (?) or to_scope in (?))", project_sid, scopes, scopes], :order => "projects.kee")
    end
  
  
  #<!--Laedt die Projects und Versions aus der Dependency Check Page-->
   
    
    metricId = Metric.by_name('dependencycheck.dependency').id
    depMeasure = ProjectMeasure.first(:include => ['measure_data'], :conditions => ['snapshot_id=? AND metric_id=?', project_sid, metricId])
    checkedDependencies = depMeasure.data.split(';')
    @entries = []
    dependencies.each do |dep|
      license = ""
      status = ""
      checkedDependencies.each do |checkedDep|
        d = checkedDep.split('~')
        if dep.to.key == d[0]
          license = d[1]
          status = d[2]
        end
      end
      if status!=''
        @entries.push([dep.to.name, dep.to_snapshot.version, license, status])
      end
    end
    @filename=Project.find(params[:id]).name

    render :template => 'export/to_csv', :layout => false
end
end