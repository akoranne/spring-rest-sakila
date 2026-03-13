const rentalRepository = require('../repositories/rentalRepository');

const findAll = async ({ page, size, sort, customerId }) => {
  const [rentals, countResult] = await Promise.all([
    rentalRepository.findAll({ page, size, sort, customerId }),
    rentalRepository.count({ customerId }),
  ]);
  const total = parseInt(countResult.total, 10);
  return { data: rentals, total, page, size };
};

const findById = async (rentalId) => {
  const rental = await rentalRepository.findById(rentalId);
  if (!rental) {
    const err = new Error(`Rental with id ${rentalId} not found`);
    err.statusCode = 404;
    err.code = 'RESOURCE_NOT_FOUND';
    throw err;
  }
  return rental;
};

const create = async (data) => {
  if (!data.rental_date) {
    data.rental_date = new Date();
  }
  const [rental] = await rentalRepository.create(data);
  return rental;
};

const update = async (rentalId, data) => {
  await findById(rentalId);
  const [rental] = await rentalRepository.update(rentalId, data);
  return rental;
};

const returnRental = async ({ rental_id, return_date }) => {
  const rental = await findById(rental_id);
  if (rental.return_date) {
    const err = new Error(`Rental with id ${rental_id} has already been returned`);
    err.statusCode = 400;
    err.code = 'VALIDATION_ERROR';
    throw err;
  }
  const [updated] = await rentalRepository.update(rental_id, {
    return_date: return_date || new Date(),
  });
  return updated;
};

const remove = async (rentalId) => {
  await findById(rentalId);
  await rentalRepository.remove(rentalId);
};

module.exports = { findAll, findById, create, update, returnRental, remove };
