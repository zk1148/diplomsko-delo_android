using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Belgrade.SqlClient;
using System.Data.SqlClient;

namespace RVK.Controllers
{
    [Produces("application/json")]
    [Route("api/PovratnicaDokument")]
    public class PovratnicaDokumentController : Controller
    {
        private readonly IQueryPipe SqlPipe;
        private readonly ICommand SqlCommand;

        public PovratnicaDokumentController(ICommand sqlCommand, IQueryPipe sqlPipe)
        {
            this.SqlCommand = sqlCommand;
            this.SqlPipe = sqlPipe;
        }


        

        [HttpPost("{zadnji_zst}/{delnalog}")]
        public async Task Post(string zadnji_zst, string delnalog)
        {

            var cmd = new SqlCommand(@" DECLARE @LoopCounter INT, @MaxZST INT = " + zadnji_zst + ", " +
                                      " @SIFART VARCHAR(10), @KOLI VARCHAR(5), @STDOK INT, " +
                                      " @STEVILO_ZAPISOV INT " +

                                      " SET @STDOK=(SELECT ISNULL(MAX(STEV),0)+1 as stdok138 FROM FAKNUMAT WHERE VT=138) " +
                                      " SET @STEVILO_ZAPISOV=(SELECT COUNT(*) FROM GIBMAT_TEMP) " +
                                      " SET @LoopCounter=" + zadnji_zst+"-(@STEVILO_ZAPISOV-1) " +

                                      " WHILE(@LoopCounter <= @MaxZST) " +
                                      " BEGIN " +
                                      "     SET @SIFART=(SELECT SIFART  " +
                                      "     FROM GIBMAT_TEMP " +
                                      "     WHERE ZST=@LoopCounter)  " +

                                      "     SET @KOLI=(SELECT VRACAM  " +
                                      "     FROM GIBMAT_TEMP " +
                                      "     WHERE ZST=@LoopCounter)  " +

                                      "     exec ZAD_N_IZDAJ_ARTI '" + delnalog+"', @SIFART, @KOLI, @STDOK, '' " +

                                      "    SET @LoopCounter  = @LoopCounter  + 1         " +
                                      " END");
            await SqlCommand.ExecuteNonQuery(cmd);
        }
        
    }
}
