using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Belgrade.SqlClient;

namespace RVK.Controllers
{
    [Produces("application/json")]
    [Route("api/PreveriDelNalog")]
    public class PreveriDelNalogController : Controller
    {
        private readonly IQueryPipe SqlPipe;
        private readonly ICommand SqlCommand;

        public PreveriDelNalogController(ICommand sqlCommand, IQueryPipe sqlPipe)
        {
            this.SqlCommand = sqlCommand;
            this.SqlPipe = sqlPipe;
        }


        [HttpGet("{id}")]
        public async Task Get(string id)
        {
            await SqlPipe.Stream(" DECLARE @STEVILO INT " +
                                 "  DECLARE @STATUS BIT " +
                                 "  SET @STEVILO = NULL " +
                                 "  SET @STATUS = 0 " +
                                 "  EXEC PREVERI_DELOVNINALOG '" + id + "', @STEVILO OUTPUT, @STATUS OUTPUT " +
                                 " select '{\"PreveriDelNalog\":[,{\"STEVILO\":\"'+cast(@STEVILO as varchar)+'\",\"STATUS\":\"'+cast(@STATUS as varchar)+'\"}]}'; ",
            Response.Body, "[]");


        }

    }
}