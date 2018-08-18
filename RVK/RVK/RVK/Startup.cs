using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Belgrade.SqlClient;
using Belgrade.SqlClient.SqlDb;
using System.Data.SqlClient;

namespace RVK
{
    public class Startup
    {
        public Startup(IHostingEnvironment env)
        {
            var builder = new ConfigurationBuilder()
                .SetBasePath(env.ContentRootPath)
                .AddJsonFile("appsettings.json", optional: false, reloadOnChange: true)
                .AddJsonFile($"appsettings.{env.EnvironmentName}.json", optional: true)
                .AddEnvironmentVariables();
            Configuration = builder.Build();
        }

        public IConfigurationRoot Configuration { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            const string ConnString = "Data Source=ALBERTNEW-PC\\SQLEXPRESS2014;Initial Catalog=RVK_27102015;Persist Security Info=True;User ID=sa;Password=rokada2000@"; //originalna
            //const string ConnString = "Data Source=RVK-SQL3\\RISP;Initial Catalog=Risp_sql;Persist Security Info=True;User ID=sa;Password=prepozno"; //RVK

            //const string ConnString = "Data Source=RVK-SQL3\\RISP;Initial Catalog=Risp_Milena1;Persist Security Info=True;User ID=sa;Password=prepozno"; 


            services.AddTransient<IQueryPipe>(_ => new QueryPipe(new SqlConnection(ConnString)));
            services.AddTransient<ICommand>(_ => new Command(new SqlConnection(ConnString)));

            // Add framework services.
            services.AddMvc();
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IHostingEnvironment env, ILoggerFactory loggerFactory)
        {
            loggerFactory.AddConsole(Configuration.GetSection("Logging"));
            loggerFactory.AddDebug();

            app.UseMvc();
        }
    }
}
