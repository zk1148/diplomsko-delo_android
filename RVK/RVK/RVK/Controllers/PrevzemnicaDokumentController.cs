using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Belgrade.SqlClient;
using System.Data.SqlClient;

namespace RVK.Controllers
{
    [Produces("application/json")]
    [Route("api/PrevzemnicaDokument")]
    public class PrevzemnicaDokumentController : Controller
    {
        private readonly IQueryPipe SqlPipe;
        private readonly ICommand SqlCommand;

        public PrevzemnicaDokumentController(ICommand sqlCommand, IQueryPipe sqlPipe)
        {
            this.SqlCommand = sqlCommand;
            this.SqlPipe = sqlPipe;
        }


        [HttpPost("{zadnji_zst}/{vrd}/{stev}")]
        public async Task Post(string zadnji_zst, string vrd, string stev)
        {

            var cmd = new SqlCommand(@" DECLARE @LoopCounter INT, @MaxZST INT = " + zadnji_zst + ", " +
                                       " @SIFART VARCHAR(10), @KOLI VARCHAR(5), @STDOK INT, " +
                                       " @STEVILO_ZAPISOV INT " +

                                " SET @STDOK = (SELECT ISNULL(MAX(STEV), 0) + 1 as stdok121 FROM FAKNUMAT WHERE VT = 121) " +
                                " SET @STEVILO_ZAPISOV=(SELECT COUNT(*) FROM NARMAT_TEMP) " +
                                " SET @LoopCounter=" + zadnji_zst + "-(@STEVILO_ZAPISOV-1) " +

                                " WHILE(@LoopCounter <= @MaxZST) " +
                                " BEGIN " +
                                "     SET @SIFART=(SELECT SIFART  " +
                                "     FROM NARMAT_TEMP " +
                                "     WHERE ZST=@LoopCounter)  " +

                                "     SET @KOLI=(SELECT PREVZEMAM  " +
                                "     FROM NARMAT_TEMP " +
                                "     WHERE ZST=@LoopCounter)  " +

                                "     exec ZAD_N_PREVZ_ARTI @SIFART, @KOLI, @STDOK, " +vrd+", "+stev+

                                "    SET @LoopCounter  = @LoopCounter  + 1         " +
                                " END");

            await SqlCommand.ExecuteNonQuery(cmd);
        }

    }
}
