const Joi = require('joi');

const updatePaymentSchema = Joi.object({
  amount: Joi.number().positive().precision(2),
  customer_id: Joi.number().integer(),
  staff_id: Joi.number().integer(),
  rental_id: Joi.number().integer(),
  payment_date: Joi.date(),
}).min(1);

module.exports = { updatePaymentSchema };
