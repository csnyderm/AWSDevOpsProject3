import { Alert, CardGroup, Grid, GridContainer } from "@trussworks/react-uswds";
import GoalCard from "../components/GoalCard";
import NewGoalCard from "../components/NewGoalCard";
import { useTranslation } from "react-i18next";
import { useGetGoalsByEmailQuery } from "../app/api/goalsApi";
import { useSelector } from "react-redux";
import { selectUserEmail } from "../app/features/userSlice";

export default function Goals() {
  const userEmail = useSelector(selectUserEmail);
  const { t } = useTranslation();

  const { data, error, isLoading } = useGetGoalsByEmailQuery(userEmail);
  console.log(userEmail)

  if (!isLoading) {
    if (data != null) {
      return (
        <>
          {data.length > 0 && (
          <h1 className="text-center margin-7 margin-top-1">
            {t("Goals.My Goals")}
          </h1>
          )}
          {data.length == 0 && (
            <>
            <h1 className="text-center margin-7 margin-top-1">
            {t("Goals.Welcome to your goals page")}!
          </h1>
          <h2 className="text-center margin-7 margin-top-neg-3 padding-top-0">
            {t("Goals.Add your first goal and start saving today")}.
          </h2>
          </>
          )}
          <GridContainer>
            <Grid>
              <CardGroup className="flex-justify-center">
                {data.map((goal, index) => {
                  const progress = Math.floor(goal.amountSaved / goal.goalAmount) * 100;
                  if (progress < 100) {
                    return (
                      <GoalCard
                        key={index}
                        _id={goal._id}
                        email={goal.email}
                        name={goal.name}
                        goalAmount={goal.goalAmount}
                        amountSaved={goal.amountSaved}
                      />
                    );
                  }
                })}
                <NewGoalCard />
              </CardGroup>
            </Grid>
          </GridContainer>

          {data.some(
            (goal) => Math.floor((goal.amountSaved / goal.goalAmount) * 100) === 100
          ) && (
            <>
              <h1 className="text-center margin-7 margin-top-5">
                {t("Goals.My Completed Goals")}
              </h1>
              <GridContainer className=" margin-bottom-5">
                <Grid>
                  <CardGroup className="flex-justify-center">
                    {data.map((goal, index) => {
                      const progress = Math.floor(goal.amountSaved / goal.goalAmount) * 100;
                      if (progress === 100) {
                        return (
                          <GoalCard
                            key={index}
                            _id={goal._id}
                            email={goal.email}
                            name={goal.name}
                            goalAmount={goal.goalAmount}
                            amountSaved={goal.amountSaved}
                          />
                        );
                      }
                    })}
                  </CardGroup>
                </Grid>
              </GridContainer>
            </>
          )}
        </>
      );
    }
  }
}
  //   } else {
  //     // Render if there are no goals
  //     return (
  //       <>
          // <h1 className="text-center margin-7 margin-bottom-neg-3">
          //   {t("Goals.Welcome to your goals page")}!
          // </h1>
          // <h2 className="text-center margin-7 padding-top-0">
          //   {t("Goals.Add your first goal and start saving today")}.
          // </h2>
  //         <GridContainer>
  //           <Grid>
  //             <CardGroup className="flex-justify-center">
  //               <NewGoalCard />
  //             </CardGroup>
  //           </Grid>
  //         </GridContainer>
  //       </>
  //     );
  //   }
  
